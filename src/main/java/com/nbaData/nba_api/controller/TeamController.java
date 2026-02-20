package com.nbaData.nba_api.controller;

import com.nbaData.nba_api.response.pojos.TeamDTO;
import com.nbaData.nba_api.service.NbaStatsService;
import com.nbaData.nba_api.service.TeamSyncService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nba/teams")
@RequiredArgsConstructor
public class TeamController {

    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

    private final NbaStatsService nbaStatsService;
    private final TeamSyncService teamSyncService;

    @GetMapping
    public Mono<ResponseEntity<List<TeamDTO>>> getAllTeams(
            @RequestParam(defaultValue = "00") String leagueId) {

        logger.info("Appel API NBA pour récupérer toutes les équipes");

        return nbaStatsService.getAllTeams(leagueId)
                .map(response -> {
                    List<TeamDTO> activeTeams = response.getTeams().stream()
                            .filter(team -> "2025".equals(team.getMaxYear()))
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(activeTeams);
                })
                .onErrorResume(e -> {
                    logger.error("Erreur lors de la récupération des équipes", e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @PostMapping("/sync")
    public Mono<ResponseEntity<String>> syncTeams(
            @RequestParam(defaultValue = "00") String leagueId) {

        logger.info("Synchronisation des équipes en base");

        return nbaStatsService.getAllTeams(leagueId)
                .map(response -> {
                    int count = teamSyncService.syncTeams(response);
                    return ResponseEntity.ok(count + " équipes synchronisées en base");
                })
                .onErrorResume(e -> {
                    logger.error("Erreur lors de la synchronisation des équipes", e);
                    return Mono.just(ResponseEntity.internalServerError().body("Erreur : " + e.getMessage()));
                });
    }

    @GetMapping("/{teamId}/details")
    public Mono<ResponseEntity<String>> getTeamDetails(
            @PathVariable String teamId) {

        if (!teamId.matches("\\d+")) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        logger.info("Appel API NBA pour le détail de l'équipe {}", teamId);

        return nbaStatsService.getTeamDetails(teamId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Erreur lors de la récupération des détails de l'équipe {}", teamId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
