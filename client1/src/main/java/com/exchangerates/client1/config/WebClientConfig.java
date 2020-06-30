package com.exchangerates.client1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author smykola
 */
@Configuration
public class WebClientConfig {

    @Value("${external.api.url}")
    private String baseUrl;

    @Bean
    public WebClient getWebClient() {
        return WebClient.builder().baseUrl(baseUrl).build();
    }


}
