package com.currencyexchange.server.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.currencyexchange.server.model.ConversionRequest;
import com.currencyexchange.server.model.ConversionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author smykola
 */
@Service
@Slf4j
@Validated
@AllArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private final CurrencyExchangeRateService rateService;

    @Override
    public Mono<ConversionResponse> exchange(ConversionRequest conversionRequest) {
        if (0 >= conversionRequest.getAmount()) {
            log.trace("Trying to exchange zero amount.");
            return Mono.just(ConversionResponse.builder()
                .converted(0.0)
                .amount(conversionRequest.getAmount())
                .from(conversionRequest.getFrom())
                .to(conversionRequest.getTo()).build());
        }

        return rateService.getRate(conversionRequest.getFrom(), conversionRequest.getTo())
            .map(rate -> ConversionResponse.builder()
                .converted(rate.getRate() * conversionRequest.getAmount())
                .amount(conversionRequest.getAmount())
                .from(conversionRequest.getFrom())
                .to(conversionRequest.getTo()).build());
    }
}
