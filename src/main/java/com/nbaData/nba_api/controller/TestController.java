package com.nbaData.nba_api.controller;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/nba")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    private final WebClient webClient;

    public TestController(WebClient.Builder webClientBuilder) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(10))
                .compress(true)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(true))
                .build();

        this.webClient = webClientBuilder
                .baseUrl("https://stats.nba.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .defaultHeader("Host", "stats.nba.com")
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .defaultHeader("Accept", "application/json, text/plain, */*")
                .defaultHeader("Accept-Language", "en-US,en;q=0.9,fr;q=0.8")
                .defaultHeader("Accept-Encoding", "gzip, deflate, br")
                .defaultHeader("Referer", "https://www.nba.com/")
                .defaultHeader("Origin", "https://www.nba.com")
                .defaultHeader("DNT", "1")
                .defaultHeader("Connection", "keep-alive")
                .defaultHeader("Sec-Fetch-Dest", "empty")
                .defaultHeader("Sec-Fetch-Mode", "cors")
                .defaultHeader("Sec-Fetch-Site", "same-site")
                .defaultHeader("x-nba-stats-origin", "stats")
                .defaultHeader("x-nba-stats-token", "true")
                .build();
    }


    @GetMapping("/player-career-stats")
    public Mono<ResponseEntity<String>> getPlayerCareerStats(
            @RequestParam(defaultValue = "2544") String playerId,
            @RequestParam(defaultValue = "Totals") String perMode) {

        logger.info("Appel API NBA pour le joueur {} avec mode {}", playerId, perMode);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats/playercareerstats")
                        .queryParam("PerMode", perMode)
                        .queryParam("PlayerID", playerId)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> {
                    logger.info("Réponse reçue avec succès");
                    return ResponseEntity.ok(body);
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("Erreur WebClient - Status: {}, Body: {}",
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.just(ResponseEntity
                            .status(ex.getStatusCode())
                            .body("Erreur API NBA: " + ex.getStatusCode() + " - " + ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex -> {
                    logger.error("Erreur générale: ", ex);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erreur serveur: " + ex.getMessage()));
                });
    }
}
