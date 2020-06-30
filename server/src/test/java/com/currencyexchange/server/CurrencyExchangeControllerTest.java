package com.currencyexchange.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.currencyexchange.server.model.ConversionRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CurrencyExchangeControllerTest {

    public static final Double RATE = 4.4684;
    private static final String EXTERNAL_SUCCESS_RESPONSE = "{\n"
        + "  \"rate\": " + RATE + "\n"
        + "}";
    @Autowired
    private WebTestClient client;

    // configure only one server to emulate shutdown of others
    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(9090);
        wireMockServer.start();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testRequestBodyNotValid() {
        ConversionRequest request = ConversionRequest.builder().build();
        client.post()
            .uri("/currency/convert")
            .body(Mono.just(request), ConversionRequest.class)
            .exchange()
            .expectStatus().isBadRequest();

        request = ConversionRequest.builder().amount(-1.0).from("AA").to("BB").build();
        client.post()
            .uri("/currency/convert")
            .body(Mono.just(request), ConversionRequest.class)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    public void testZeroAmountConversion() {
        ConversionRequest request = ConversionRequest.builder().amount(0.0).from("AAA").to("BBB").build();
        client.post()
            .uri("/currency/convert")
            .body(Mono.just(request), ConversionRequest.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.converted").isEqualTo(0.0);
    }

    @Test
    public void testNoProvidersAvailable() {
        wireMockServer.stubFor(get("/exchangerate?base=AAA&to=BBB")).setResponse(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(HttpStatus.NOT_FOUND.value())
            .build());
        ConversionRequest request = ConversionRequest.builder().amount(1.0).from("AAA").to("BBB").build();
        client.post()
            .uri("/currency/convert")
            .body(Mono.just(request), ConversionRequest.class)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
            .expectBody()
            .jsonPath("$.message").isEqualTo("No providers available");
    }

    @Test
    public void testExchange() {
        wireMockServer.stubFor(get("/exchangerate?base=AAA&to=BBB")).setResponse(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(HttpStatus.OK.value())
            .withBody(EXTERNAL_SUCCESS_RESPONSE)
            .build());
        final double amount = 2.0;
        ConversionRequest request = ConversionRequest.builder().amount(amount).from("AAA").to("BBB").build();
        client.post()
            .uri("/currency/convert")
            .body(Mono.just(request), ConversionRequest.class)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody()
            .jsonPath("$.converted").isEqualTo(RATE * amount);

    }
}
