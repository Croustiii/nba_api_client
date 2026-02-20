package com.nbaData.nba_api.controller;

import com.nbaData.nba_api.response.LeagueGameLogResponse;
import com.nbaData.nba_api.service.GameSyncService;
import com.nbaData.nba_api.service.NbaStatsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/nba/games")
@RequiredArgsConstructor
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final NbaStatsService nbaStatsService;
    private final GameSyncService gameSyncService;

    /**
     * Methode de récupération des données de Match
     * @param season
     * @param leagueId
     * @return
     */
    @GetMapping
    public Mono<ResponseEntity<LeagueGameLogResponse>> getGames(
            @RequestParam(defaultValue = "2024-25") String season,
            @RequestParam(defaultValue = "00") String leagueId) {

        logger.info("Appel API NBA pour récupérer les matchs, saison {}", season);

        return nbaStatsService.getLeagueGameLog(season, leagueId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Erreur lors de la récupération des matchs", e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    /**
     * Methode pour persister les données en base
     * @param season
     * @param leagueId
     * @return
     */
    @PostMapping("/sync")
    public Mono<ResponseEntity<String>> syncGames(
            @RequestParam(defaultValue = "2024-25") String season,
            @RequestParam(defaultValue = "00") String leagueId) {

        logger.info("Synchronisation des matchs en base, saison {}", season);

        return nbaStatsService.getLeagueGameLog(season, leagueId)
                .map(response -> {
                    int count = gameSyncService.syncGames(response);
                    return ResponseEntity.ok(count + " lignes de matchs synchronisées en base");
                })
                .onErrorResume(e -> {
                    logger.error("Erreur lors de la synchronisation des matchs", e);
                    return Mono.just(ResponseEntity.internalServerError().body("Erreur : " + e.getMessage()));
                });
    }

}
