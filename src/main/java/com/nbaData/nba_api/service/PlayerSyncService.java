package com.nbaData.nba_api.service;

import com.nbaData.nba_api.entity.Player;
import com.nbaData.nba_api.repository.PlayerRepository;
import com.nbaData.nba_api.response.PlayerIndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PlayerSyncService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerSyncService.class);

    private final PlayerRepository playerRepository;

    public PlayerSyncService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
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
}
