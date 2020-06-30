package com.currencyexchange.server.config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for clients
 *
 * Note! Could be replaced with external discovery services
 *
 * @author smykola
 */
@Configuration
public class ExchangeRateClientsConfig {

    @Value("${exchangerate.clients}")
    String exchangeRatesClientUrls;

    @Bean
    public List<WebClient> getClients() {
        return Stream.of(exchangeRatesClientUrls.split(",")).map(WebClient::create).collect(Collectors.toList());
    }


}
