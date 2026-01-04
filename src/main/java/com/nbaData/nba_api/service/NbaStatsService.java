package com.nbaData.nba_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;


/**
 * Service pour gérer les appels à l'API NBA Stats
 */
@Service
public class NbaStatsService {

    private static final Logger logger = LoggerFactory.getLogger(NbaStatsService.class);

    private static final String NBA_STATS_BASE_URL = "https://stats.nba.com/stats";
    private final WebClient webClient;

    public NbaStatsService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .clone()
                .baseUrl(NBA_STATS_BASE_URL)
                .defaultHeader("Host", "stats.nba.com")
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .defaultHeader("Accept", "application/json, text/plain, */*")
                .defaultHeader("Accept-Language", "en-US,en;q=0.9")
                .defaultHeader("Referer", "https://www.nba.com/")
                .defaultHeader("Origin", "https://www.nba.com")
                .build();
    }

    /**
     * Récupère les statistiques de carrière d'un joueur
     */
    public String getPlayerCareerStats(String playerId, String perMode) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/playercareerstats")
                        .queryParam("PlayerID", playerId)
                        .queryParam("PerMode", perMode)
                        .build())
                .headers(httpHeaders -> {
                    HttpHeaders headers = createNbaHeaders();
                    headers.forEach((k, v) -> httpHeaders.put(k, v));
                    // Headers souvent requis par stats.nba.com
                    httpHeaders.set("x-nba-stats-origin", "stats");
                    httpHeaders.set("x-nba-stats-token", "true");
                })
                .retrieve()
                .onStatus(status -> status.isError(), resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    logger.error("NBA API returned error {} with body: {}", resp.statusCode(), body);
                                    return Mono.error(new RuntimeException("NBA API error: " + resp.statusCode()));
                                })
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30)) // Timeout de 30 secondes
                .block(); // Convertit en appel synchrone


    }

    /**
     * Crée les headers HTTP requis par l'API NBA
     */
    private HttpHeaders createNbaHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "stats.nba.com");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        headers.set("Accept", "application/json, text/plain, */*");
        headers.set("Accept-Language", "en-US,en;q=0.9");
        //headers.set("Accept-Encoding", "gzip, deflate, br");
        headers.set("Connection", "keep-alive");
        headers.set("Referer", "https://www.nba.com/");
        headers.set("Origin", "https://www.nba.com");
        return headers;
    }
}
