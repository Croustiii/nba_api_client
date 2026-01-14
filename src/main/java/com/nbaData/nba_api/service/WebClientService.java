package com.nbaData.nba_api.service;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WebClientService {
    private static final Logger logger = LoggerFactory.getLogger(WebClientService.class);

    private WebClient webClient = null;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebClientService(WebClient.Builder webClientBuilder) {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(10))
                .compress(true)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().enableLoggingRequestDetails(true);
                    // Augmente la limite de buffer si nécessaire (par défaut 256KB)
                    configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024); // 16MB
                })
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

    public Mono<ResponseEntity<String>> performGetCall(Map<String, String> params, String endPoint){
        // Convertit Map<String,String> en MultiValueMap<String,String>
        MultiValueMap<String, String> multiParams = new LinkedMultiValueMap<>();
        if (params != null) {
            params.forEach(multiParams::add);
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(endPoint)
                        .queryParams(multiParams)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(body -> {
                    try {
                        JsonNode json = objectMapper.readTree(body);
                        String pretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                        return Mono.just(ResponseEntity
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(pretty));
                    } catch (Exception e) {
                        logger.error("Erreur parsing JSON", e);
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Erreur parse JSON: " + e.getMessage()));
                    }
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
