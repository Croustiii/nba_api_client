package com.nbaData.nba_api.controller;

import com.nbaData.nba_api.service.NbaStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/nba")
public class PlayerController {
    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    public final NbaStatsService nbaStatsService;

    @Autowired
    public PlayerController(NbaStatsService nbaStatsService) {
        this.nbaStatsService = nbaStatsService;
    }


    @GetMapping("/player-career-stats")
    public Mono<ResponseEntity<String>> getPlayerCareerStats(
            @RequestParam(defaultValue = "2544") String playerId,
            @RequestParam(defaultValue = "Totals") String perMode) {

        logger.info("Appel API NBA pour le joueur {} avec mode {}", playerId, perMode);

        return this.nbaStatsService.getCareerStats(playerId, perMode);
    }



}
