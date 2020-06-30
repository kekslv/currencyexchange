package com.currencyexchange.server.service;

import com.currencyexchange.server.model.ConversionRequest;
import com.currencyexchange.server.model.ConversionResponse;
import reactor.core.publisher.Mono;

/**
 * @author smykola
 */
public interface CurrencyExchangeService {


    /**
     * Converts provided amount of one currency to another.
     *
     * @return conversion result
     */
    Mono<ConversionResponse> exchange(ConversionRequest conversionRequest);

}
