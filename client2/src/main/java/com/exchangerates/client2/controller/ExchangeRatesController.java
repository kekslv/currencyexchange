package com.exchangerates.client2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.exchangerates.client2.model.ExchangeRate;
import com.exchangerates.client2.model.ExchangeRateDTO;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;
import reactor.core.publisher.Mono;

/**
 * Provide operations to communicate with external service to get currency exchange rates
 *
 * @author smykola
 */
@RestController
@Validated
@AllArgsConstructor
public class ExchangeRatesController {
    private final WebClient webClient;

    /**
     * Returns currency exchange rates
     *
     * @param base string representing the currency to exchange
     * @param to string representing the currency to which conversion should be applied
     * @return currency exchange rate for given <b>base</b> and <b>to</b> currencies
     */
    @GetMapping(value = "/exchangerate")
    public Mono<ExchangeRate> getLatest(@RequestParam("base") @Length(min = 3, max = 3) String base,
        @RequestParam("to") @Length(min = 3, max = 3) String to) {
        return webClient
            .get()
            .uri(uriBuilder -> uriBuilder
                .path("/v4/latest/{base}")
                .build(base))
            .retrieve()
            .onStatus(HttpStatus::isError,
                response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Exchange rate not found")))
            .bodyToMono(ExchangeRateDTO.class)
            .filter(response -> !CollectionUtils.isEmpty(response.getRates()))
            .filter(response -> response.getRates().containsKey(to))
            .map(response -> new ExchangeRate(response.getRates().get(to)))
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Exchange rate not found")));
    }
}
