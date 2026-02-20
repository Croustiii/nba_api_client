package com.nbaData.nba_api.service;

import com.nbaData.nba_api.entity.Game;
import com.nbaData.nba_api.repository.GameRepository;
import com.nbaData.nba_api.response.LeagueGameLogResponse;
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
public class GameSyncService {

    private static final Logger logger = LoggerFactory.getLogger(GameSyncService.class);

    private final GameRepository gameRepository;

    public GameSyncService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Transactional
    public int syncGames(LeagueGameLogResponse response) {
        LeagueGameLogResponse.ResultSet resultSet = response.getResultSets().get(0);
        List<String> headers = resultSet.getHeaders();
        Map<String, Integer> headerIndex = IntStream.range(0, headers.size())
                .boxed()
                .collect(Collectors.toMap(headers::get, i -> i));

        List<Game> games = new ArrayList<>();
        for (List<Object> row : resultSet.getRowSet()) {
            Game game = mapRowToGame(row, headerIndex);
            if (game != null) {
                games.add(game);
            }
        }

        gameRepository.saveAll(games);
        logger.info("{} lignes de matchs synchronisées en base", games.size());
        return games.size();
    }

    private Game mapRowToGame(List<Object> row, Map<String, Integer> headerIndex) {
        String gameId = getString(row, headerIndex, "GAME_ID");
        Long teamId = getLong(row, headerIndex, "TEAM_ID");
        if (gameId == null || teamId == null) return null;

        Game game = new Game();
        game.setGameId(gameId);
        game.setTeamId(teamId);
        game.setSeasonId(getString(row, headerIndex, "SEASON_ID"));
        game.setTeamAbbreviation(getString(row, headerIndex, "TEAM_ABBREVIATION"));
        game.setTeamName(getString(row, headerIndex, "TEAM_NAME"));
        game.setGameDate(getString(row, headerIndex, "GAME_DATE"));
        game.setMatchup(getString(row, headerIndex, "MATCHUP"));
        game.setWl(getString(row, headerIndex, "WL"));
        game.setMin(getInteger(row, headerIndex, "MIN"));
        game.setFgm(getInteger(row, headerIndex, "FGM"));
        game.setFga(getInteger(row, headerIndex, "FGA"));
        game.setFgPct(getDouble(row, headerIndex, "FG_PCT"));
        game.setFg3m(getInteger(row, headerIndex, "FG3M"));
        game.setFg3a(getInteger(row, headerIndex, "FG3A"));
        game.setFg3Pct(getDouble(row, headerIndex, "FG3_PCT"));
        game.setFtm(getInteger(row, headerIndex, "FTM"));
        game.setFta(getInteger(row, headerIndex, "FTA"));
        game.setFtPct(getDouble(row, headerIndex, "FT_PCT"));
        game.setOreb(getInteger(row, headerIndex, "OREB"));
        game.setDreb(getInteger(row, headerIndex, "DREB"));
        game.setReb(getInteger(row, headerIndex, "REB"));
        game.setAst(getInteger(row, headerIndex, "AST"));
        game.setStl(getInteger(row, headerIndex, "STL"));
        game.setBlk(getInteger(row, headerIndex, "BLK"));
        game.setTov(getInteger(row, headerIndex, "TOV"));
        game.setPf(getInteger(row, headerIndex, "PF"));
        game.setPts(getInteger(row, headerIndex, "PTS"));
        game.setPlusMinus(getDouble(row, headerIndex, "PLUS_MINUS"));
        return game;
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
