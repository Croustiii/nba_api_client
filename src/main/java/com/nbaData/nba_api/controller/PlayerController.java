package com.nbaData.nba_api.controller;

import com.nbaData.nba_api.response.PlayerCareerStatsResponse;
import com.nbaData.nba_api.response.PlayerIndexResponse;
import com.nbaData.nba_api.service.CsvExportService;
import com.nbaData.nba_api.service.NbaStatsService;
import com.nbaData.nba_api.service.PlayerSyncService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/nba/players")
@RequiredArgsConstructor
public class PlayerController {
    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    private final NbaStatsService nbaStatsService;
    private final CsvExportService csvExportService;
    private final PlayerSyncService playerSyncService;

    @GetMapping
    public Mono<ResponseEntity<PlayerIndexResponse>> getAllPlayers(
            @RequestParam(defaultValue = "2024-25") String season,
            @RequestParam(defaultValue = "00") String leagueId) {

        logger.info("Appel API NBA pour récupérer tous les joueurs, saison {}", season);

        return nbaStatsService.getAllPlayers(season, leagueId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Erreur lors de la récupération des joueurs", e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @GetMapping("/export-csv")
    public Mono<ResponseEntity<String>> exportPlayersToCsv(
            @RequestParam(defaultValue = "2024-25") String season,
            @RequestParam(defaultValue = "00") String leagueId) {

        logger.info("Export CSV des joueurs, saison {}", season);

        return nbaStatsService.getAllPlayers(season, leagueId)
                .map(response -> {
                    try {
                        Path path = csvExportService.exportPlayersToCsv(response, "exports/players_" + season + ".csv");
                        return ResponseEntity.ok("Fichier CSV créé : " + path.toAbsolutePath());
                    } catch (Exception e) {
                        logger.error("Erreur lors de l'export CSV", e);
                        return ResponseEntity.internalServerError().body("Erreur lors de l'export CSV : " + e.getMessage());
                    }
                })
                .onErrorResume(e -> {
                    logger.error("Erreur lors de la récupération des joueurs pour export CSV", e);
                    return Mono.just(ResponseEntity.internalServerError().body("Erreur lors de la récupération des joueurs"));
                });
    }

    @PostMapping("/sync")
    public Mono<ResponseEntity<String>> syncPlayers(
            @RequestParam(defaultValue = "2024-25") String season,
            @RequestParam(defaultValue = "00") String leagueId) {

        logger.info("Synchronisation des joueurs en base, saison {}", season);

        return nbaStatsService.getAllPlayers(season, leagueId)
                .map(response -> {
                    int count = playerSyncService.syncPlayers(response);
                    return ResponseEntity.ok(count + " joueurs synchronisés en base");
                })
                .onErrorResume(e -> {
                    logger.error("Erreur lors de la synchronisation des joueurs", e);
                    return Mono.just(ResponseEntity.internalServerError().body("Erreur : " + e.getMessage()));
                });
    }

    @PostMapping("/sync-career-stats")
    public Mono<ResponseEntity<String>> syncCareerStats(
            @RequestParam(defaultValue = "10") int limit) {

        logger.info("Synchronisation des career stats pour {} joueurs", limit);

        return playerSyncService.syncAllCareerStats(limit)
                .map(count -> ResponseEntity.ok(count + " lignes de career stats synchronisées"))
                .onErrorResume(e -> {
                    logger.error("Erreur lors de la synchronisation des career stats", e);
                    return Mono.just(ResponseEntity.internalServerError().body("Erreur : " + e.getMessage()));
                });
    }

    @GetMapping("/{playerId}/career-stats")
    public Mono<ResponseEntity<PlayerCareerStatsResponse>> getPlayerCareerStats(
            @PathVariable String playerId,
            @RequestParam(defaultValue = "Totals") String perMode) {

        if (!playerId.matches("\\d+")) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        logger.info("Appel API NBA pour le joueur {} avec mode {}", playerId, perMode);

        return nbaStatsService.getCareerStats(playerId, perMode)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Erreur lors de l'appel API NBA pour le joueur {}", playerId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
