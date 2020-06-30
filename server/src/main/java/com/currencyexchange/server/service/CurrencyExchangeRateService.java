package com.currencyexchange.server.service;

import com.currencyexchange.server.model.ExchangeRate;
import reactor.core.publisher.Mono;

/**
 * @author smykola
 */
public interface CurrencyExchangeRateService {

    /**
     * Returns exchange rate for provided currencies
     *
     * @param base the currency to exchange
     * @param to the currency to which conversion should be applied
     * @return exchange rate for given <b>base</b> and <b>to</b> currencies
     */
    Mono<ExchangeRate> getRate(String base, String to);

}
