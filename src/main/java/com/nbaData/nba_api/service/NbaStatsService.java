package com.nbaData.nba_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;


/**
 * Service pour gérer les appels à l'API NBA Stats
 */
@Service
public class NbaStatsService {

    private static final Logger logger = LoggerFactory.getLogger(NbaStatsService.class);

    private WebClientService webClientService;

    private static final String NBA_STATS_BASE_URL = "https://stats.nba.com/stats";
    private static final String CAREER_STATS_ENPOINT_URI = "/stats/playercareerstats";

    @Autowired
    public NbaStatsService(WebClientService webClientService) {
        this.webClientService = webClientService;
    }

    public Mono<ResponseEntity<String>> getCareerStats(String playerId, String perMode) {
        HashMap<String, String> params = new HashMap<>();
        params.put("playerId", playerId);
        params.put("perMode", perMode);

        return webClientService.performGetCall(params, CAREER_STATS_ENPOINT_URI);
    }
}
