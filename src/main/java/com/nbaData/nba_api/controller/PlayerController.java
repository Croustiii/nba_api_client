package com.nbaData.nba_api.controller;

import com.nbaData.nba_api.service.NbaStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;

@RestController
@RequestMapping("/player")
@CrossOrigin(origins = "*")
public class PlayerController {


    private final NbaStatsService nbaStatsService;

    @Autowired
    public PlayerController(NbaStatsService nbaStatsService) {
        this.nbaStatsService = nbaStatsService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<String> getRoot(@PathVariable Long id) {
        return ResponseEntity.ok("player root" + id);
    }


    /**
     * Récupère les statistiques de carrière d'un joueur
     *
     * @param playerId ID du joueur (ex: 2544 pour LeBron James)
     * @param perMode Mode de calcul : "Totals", "PerGame", "Per36", etc. (défaut: "Totals")
     * @return Statistiques de carrière du joueur
     */
    @GetMapping("/career-stats")
    public ResponseEntity<String> getPlayerCareerStats(
            @RequestParam String playerId,
            @RequestParam(defaultValue = "Totals") String perMode) {

        String response = nbaStatsService.getPlayerCareerStats(playerId, perMode);
        return ResponseEntity.ok(response);
    }

}
