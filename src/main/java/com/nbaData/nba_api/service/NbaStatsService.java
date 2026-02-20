package com.nbaData.nba_api.service;

import com.nbaData.nba_api.response.CommonTeamYearsResponse;
import com.nbaData.nba_api.response.PlayerCareerStatsResponse;
import com.nbaData.nba_api.response.PlayerIndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service pour gérer les appels à l'API NBA Stats
 */
@Service
public class NbaStatsService {

    private static final Logger logger = LoggerFactory.getLogger(NbaStatsService.class);

    private WebClientService webClientService;
    private final boolean filterActivePlayers;

    private static final String CAREER_STATS_ENPOINT_URI = "/stats/playercareerstats";
    private static final String COMMON_TEAM_YEARS_ENPOINT_URI = "/stats/commonteamyears";
    private static final String TEAM_DETAILS_ENPOINT_URI = "/stats/teamdetails";
    private static final String PLAYER_INDEX_ENDPOINT_URI = "/stats/playerindex";

    @Autowired
    public NbaStatsService(WebClientService webClientService,
                           @Value("${nba.api.filter-active-players:true}") boolean filterActivePlayers) {
        this.webClientService = webClientService;
        this.filterActivePlayers = filterActivePlayers;
    }

    public Mono<PlayerCareerStatsResponse> getCareerStats(String playerId, String perMode) {
        HashMap<String, String> params = new HashMap<>();
        params.put("playerId", playerId);
        params.put("perMode", perMode);

        return webClientService.performGetCall(params, CAREER_STATS_ENPOINT_URI, PlayerCareerStatsResponse.class);
    }

    public Mono<CommonTeamYearsResponse> getAllTeams (String leagueId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("LeagueID", leagueId);

        return webClientService.performGetCall(params, COMMON_TEAM_YEARS_ENPOINT_URI, CommonTeamYearsResponse.class);
    }

    public Mono<PlayerIndexResponse> getAllPlayers(String season, String leagueId) {
        HashMap<String, String> params = getStringStringHashMap(season, leagueId);

        return webClientService.performGetCall(params, PLAYER_INDEX_ENDPOINT_URI, PlayerIndexResponse.class)
                .map(response -> {
                    if (response.getResultSets() != null && filterActivePlayers) {
                        for (PlayerIndexResponse.ResultSet resultSet : response.getResultSets()) {
                            int activeIndex = findHeaderIndex(resultSet.getHeaders(),
                                    "ROSTER_STATUS", "IS_ACTIVE_FLAG", "IS_ACTIVE");
                            if (activeIndex >= 0) {
                                int idx = activeIndex;
                                List<List<Object>> filtered = resultSet.getRowSet().stream()
                                        .filter(row -> {
                                            if (row.size() <= idx) return false;
                                            Object val = row.get(idx);
                                            if (val == null) return false;
                                            if (val instanceof Number) return ((Number) val).intValue() == 1;
                                            return "1".equals(String.valueOf(val));
                                        })
                                        .collect(Collectors.toList());
                                resultSet.setRowSet(filtered);
                            }
                        }
                    }
                    return response;
                });
    }

    private HashMap<String, String> getStringStringHashMap(String season, String leagueId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("College", "");
        params.put("Country", "");
        params.put("DraftPick", "");
        params.put("DraftRound", "");
        params.put("DraftYear", "");
        params.put("Height", "");
        params.put("Historical", "1");
        params.put("LeagueID", leagueId);
        params.put("Season", season);
        params.put("SeasonType", "Regular Season");
        params.put("TeamID", "0");
        params.put("Weight", "");
        return params;
    }

    private int findHeaderIndex(List<String> headers, String... candidates) {
        for (String candidate : candidates) {
            int idx = headers.indexOf(candidate);
            if (idx >= 0) return idx;
        }
        return -1;
    }

    public Mono<String> getTeamDetails (String teamId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("TeamID", teamId);

        return webClientService.performGetCall(params, TEAM_DETAILS_ENPOINT_URI);
    }

}
