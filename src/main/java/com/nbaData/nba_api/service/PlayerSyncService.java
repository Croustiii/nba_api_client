package com.nbaData.nba_api.service;

import com.nbaData.nba_api.entity.Player;
import com.nbaData.nba_api.entity.PlayerCareerStats;
import com.nbaData.nba_api.repository.PlayerCareerStatsRepository;
import com.nbaData.nba_api.repository.PlayerRepository;
import com.nbaData.nba_api.response.PlayerCareerStatsResponse;
import com.nbaData.nba_api.response.PlayerIndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PlayerSyncService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerSyncService.class);

    private final PlayerRepository playerRepository;
    private final PlayerCareerStatsRepository careerStatsRepository;
    private final NbaStatsService nbaStatsService;
    private final long syncDelayMs;

    public PlayerSyncService(PlayerRepository playerRepository,
                             PlayerCareerStatsRepository careerStatsRepository,
                             NbaStatsService nbaStatsService,
                             @Value("${nba.api.sync-delay-ms:500}") long syncDelayMs) {
        this.playerRepository = playerRepository;
        this.careerStatsRepository = careerStatsRepository;
        this.nbaStatsService = nbaStatsService;
        this.syncDelayMs = syncDelayMs;
    }

    @Transactional
    public int syncPlayers(PlayerIndexResponse response) {
        PlayerIndexResponse.ResultSet resultSet = response.getResultSets().get(0);
        List<String> headers = resultSet.getHeaders();
        Map<String, Integer> headerIndex = IntStream.range(0, headers.size())
                .boxed()
                .collect(Collectors.toMap(headers::get, i -> i));

        List<Player> players = new ArrayList<>();
        for (List<Object> row : resultSet.getRowSet()) {
            Player player = mapRowToPlayer(row, headerIndex);
            if (player != null) {
                players.add(player);
            }
        }

        playerRepository.saveAll(players);
        logger.info("{} joueurs synchronisés en base", players.size());
        return players.size();
    }

    private Player mapRowToPlayer(List<Object> row, Map<String, Integer> headerIndex) {
        Long personId = getLong(row, headerIndex, "PERSON_ID");
        if (personId == null) return null;

        Player player = new Player();
        player.setPersonId(personId);
        player.setLastName(getString(row, headerIndex, "PLAYER_LAST_NAME"));
        player.setFirstName(getString(row, headerIndex, "PLAYER_FIRST_NAME"));
        player.setPlayerSlug(getString(row, headerIndex, "PLAYER_SLUG"));
        player.setTeamId(getLong(row, headerIndex, "TEAM_ID"));
        player.setTeamAbbreviation(getString(row, headerIndex, "TEAM_ABBREVIATION"));
        player.setTeamName(getString(row, headerIndex, "TEAM_NAME"));
        player.setTeamCity(getString(row, headerIndex, "TEAM_CITY"));
        player.setJerseyNumber(getString(row, headerIndex, "JERSEY_NUMBER"));
        player.setPosition(getString(row, headerIndex, "POSITION"));
        player.setHeight(getString(row, headerIndex, "HEIGHT"));
        player.setWeight(getString(row, headerIndex, "WEIGHT"));
        player.setCollege(getString(row, headerIndex, "COLLEGE"));
        player.setCountry(getString(row, headerIndex, "COUNTRY"));
        player.setDraftYear(getInteger(row, headerIndex, "DRAFT_YEAR"));
        player.setDraftRound(getInteger(row, headerIndex, "DRAFT_ROUND"));
        player.setDraftNumber(getInteger(row, headerIndex, "DRAFT_NUMBER"));
        player.setRosterStatus(getDouble(row, headerIndex, "ROSTER_STATUS"));
        player.setPts(getDouble(row, headerIndex, "PTS"));
        player.setReb(getDouble(row, headerIndex, "REB"));
        player.setAst(getDouble(row, headerIndex, "AST"));
        player.setStatsTimeframe(getString(row, headerIndex, "STATS_TIMEFRAME"));
        player.setFromYear(getInteger(row, headerIndex, "FROM_YEAR"));
        player.setToYear(getInteger(row, headerIndex, "TO_YEAR"));
        return player;
    }

    private Object getVal(List<Object> row, Map<String, Integer> headerIndex, String header) {
        Integer idx = headerIndex.get(header);
        if (idx == null || idx >= row.size()) return null;
        return row.get(idx);
    }

    private String getString(List<Object> row, Map<String, Integer> headerIndex, String header) {
        Object val = getVal(row, headerIndex, header);
        return val != null ? val.toString() : null;
    }

    private Long getLong(List<Object> row, Map<String, Integer> headerIndex, String header) {
        Object val = getVal(row, headerIndex, header);
        if (val instanceof Number) return ((Number) val).longValue();
        if (val instanceof String) {
            try { return Long.parseLong((String) val); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private Integer getInteger(List<Object> row, Map<String, Integer> headerIndex, String header) {
        Object val = getVal(row, headerIndex, header);
        if (val instanceof Number) return ((Number) val).intValue();
        if (val instanceof String) {
            try { return Integer.parseInt((String) val); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private Double getDouble(List<Object> row, Map<String, Integer> headerIndex, String header) {
        Object val = getVal(row, headerIndex, header);
        if (val instanceof Number) return ((Number) val).doubleValue();
        if (val instanceof String) {
            try { return Double.parseDouble((String) val); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    public Mono<Integer> syncAllCareerStats(int limit) {
        List<Player> players = playerRepository.findAll(PageRequest.of(0, limit)).getContent();
        logger.info("Sync career stats pour {} joueurs", players.size());

        AtomicInteger totalRows = new AtomicInteger(0);

        return Flux.fromIterable(players)
                .concatMap(player -> Mono.delay(Duration.ofMillis(syncDelayMs))
                        .then(nbaStatsService.getCareerStats(String.valueOf(player.getPersonId()), "Totals"))
                        .doOnNext(response -> {
                            int saved = saveCareerStats(response, player.getPersonId());
                            totalRows.addAndGet(saved);
                            logger.info("Joueur {} {} : {} saisons synchronisées",
                                    player.getFirstName(), player.getLastName(), saved);
                        })
                        .onErrorResume(e -> {
                            logger.warn("Erreur pour le joueur {} ({}): {}",
                                    player.getPersonId(), player.getLastName(), e.getMessage());
                            return Mono.empty();
                        })
                )
                .then(Mono.fromSupplier(totalRows::get));
    }

    @Transactional
    protected int saveCareerStats(PlayerCareerStatsResponse response, Long playerId) {
        if (response.getResultSets() == null) return 0;

        PlayerCareerStatsResponse.ResultSet resultSet = response.getResultSets().stream()
                .filter(rs -> "SeasonTotalsRegularSeason".equals(rs.getName()))
                .findFirst()
                .orElse(null);

        if (resultSet == null || resultSet.getHeaders() == null) return 0;

        List<String> headers = resultSet.getHeaders();
        Map<String, Integer> headerIndex = IntStream.range(0, headers.size())
                .boxed()
                .collect(Collectors.toMap(headers::get, i -> i));

        List<PlayerCareerStats> statsList = new ArrayList<>();
        for (List<Object> row : resultSet.getRowSet()) {
            PlayerCareerStats stats = mapRowToCareerStats(row, headerIndex, playerId);
            if (stats != null) {
                statsList.add(stats);
            }
        }

        careerStatsRepository.saveAll(statsList);
        return statsList.size();
    }

    private PlayerCareerStats mapRowToCareerStats(List<Object> row, Map<String, Integer> headerIndex, Long playerId) {
        String seasonId = getString(row, headerIndex, "SEASON_ID");
        if (seasonId == null) return null;

        PlayerCareerStats stats = new PlayerCareerStats();
        stats.setPlayerId(playerId);
        stats.setSeasonId(seasonId);
        stats.setLeagueId(getString(row, headerIndex, "LEAGUE_ID"));
        stats.setTeamId(getLong(row, headerIndex, "TEAM_ID"));
        stats.setTeamAbbreviation(getString(row, headerIndex, "TEAM_ABBREVIATION"));
        stats.setPlayerAge(getDouble(row, headerIndex, "PLAYER_AGE"));
        stats.setGp(getInteger(row, headerIndex, "GP"));
        stats.setGs(getInteger(row, headerIndex, "GS"));
        stats.setMin(getDouble(row, headerIndex, "MIN"));
        stats.setFgm(getDouble(row, headerIndex, "FGM"));
        stats.setFga(getDouble(row, headerIndex, "FGA"));
        stats.setFgPct(getDouble(row, headerIndex, "FG_PCT"));
        stats.setFg3m(getDouble(row, headerIndex, "FG3M"));
        stats.setFg3a(getDouble(row, headerIndex, "FG3A"));
        stats.setFg3Pct(getDouble(row, headerIndex, "FG3_PCT"));
        stats.setFtm(getDouble(row, headerIndex, "FTM"));
        stats.setFta(getDouble(row, headerIndex, "FTA"));
        stats.setFtPct(getDouble(row, headerIndex, "FT_PCT"));
        stats.setOreb(getDouble(row, headerIndex, "OREB"));
        stats.setDreb(getDouble(row, headerIndex, "DREB"));
        stats.setReb(getDouble(row, headerIndex, "REB"));
        stats.setAst(getDouble(row, headerIndex, "AST"));
        stats.setStl(getDouble(row, headerIndex, "STL"));
        stats.setBlk(getDouble(row, headerIndex, "BLK"));
        stats.setTov(getDouble(row, headerIndex, "TOV"));
        stats.setPf(getDouble(row, headerIndex, "PF"));
        stats.setPts(getDouble(row, headerIndex, "PTS"));
        return stats;
    }
}
