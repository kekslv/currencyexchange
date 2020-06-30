package com.currencyexchange.server.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.currencyexchange.server.model.ExchangeRate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author smykola
 */
@Service
@AllArgsConstructor
@Slf4j
public class CurrencyExchangeRateServiceImpl implements CurrencyExchangeRateService {

    private List<WebClient> webClients;

    @Override
    public Mono<ExchangeRate> getRate(String base, String to) {
        return Flux.fromIterable(randomWebClientsIndexes())
            .concatMap(i -> getRate(webClients.get(i), base, to))
            .takeUntil(exchangeRate -> null != exchangeRate.getRate()).next();
    }

    /**
     * Generates random indexes for each WebClient
     */
    List<Integer> randomWebClientsIndexes() {
        List<Integer> collect = IntStream.range(0, webClients.size()).boxed().collect(Collectors.toList());
        Collections.shuffle(collect);
        return collect;
    }

    /**
     * Retrieves currency exchange rate using specified WebClient
     */
    Mono<ExchangeRate> getRate(WebClient webClient, String base, String to) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/exchangerate")
                .queryParam("base", base)
                .queryParam("to", to)
                .build()
            )
            .retrieve()
            .bodyToMono(ExchangeRate.class)
            .onErrorResume(ex -> Mono.empty());
    }
}
