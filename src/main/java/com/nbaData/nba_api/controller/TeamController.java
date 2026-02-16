package com.nbaData.nba_api.controller;

import com.nbaData.nba_api.response.pojos.Team;
import com.nbaData.nba_api.service.NbaStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nba/team")
public class TeamController {

    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    public final NbaStatsService nbaStatsService;


    public TeamController(NbaStatsService nbaStatsService) {
        this.nbaStatsService = nbaStatsService;
    }

    @GetMapping("/teams")
    public Mono<ResponseEntity<List<Team>>> getAllTeams(
            @RequestParam(defaultValue = "00") String leagueId
    ) {
        return nbaStatsService.getAllTeams(leagueId)
                .map(response -> {
                    List<Team> teams = response.getTeams();

                    // Tu peux manipuler les données
                    teams.forEach(team ->
                            logger.info("Équipe: {} - {}", team.getAbbreviation(), team.getTeamId())
                    );

                    // Filtrer les équipes actives
                    List<Team> activeTeams = teams.stream()
                            .filter(team -> "2025".equals(team.getMaxYear()))
                            .collect(Collectors.toList());

                    return ResponseEntity.ok(activeTeams);
                });
    }


    @GetMapping("/teamDetails")
    public Mono<String> getTeamDetails(
            @RequestParam(defaultValue = "1610612759") String teamId
    ) {
        logger.info("Appel API NBA pour le detail d'une equipe {}", teamId);
        var teamDetails = this.nbaStatsService.getTeamDetails(teamId);

        return teamDetails;
    }



}
