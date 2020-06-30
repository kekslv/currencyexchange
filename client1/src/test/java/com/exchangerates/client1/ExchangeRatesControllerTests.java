package com.exchangerates.client1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ExchangeRatesControllerTests {

    private static final String EXTERNAL_SUCCESS_RESPONSE = "{\"base\":\"AAA\",\"date\":\"2020-06-28\","
        + "\"rates\":{\"AAA\":1,\"BBB\":2,\"CCC\":3}}";
    @Autowired
    private WebTestClient client;

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
    void testRequestParamsMissing() {
        client.get()
            .uri("/exchangerate")
            .exchange()
            .expectStatus().isBadRequest();

        client.get()
            .uri("/exchangerate?base=AAA")
            .exchange()
            .expectStatus().isBadRequest();

        client.get()
            .uri("/exchangerate?to=AAA")
            .exchange()
            .expectStatus().isBadRequest();
    }

    //TODO: validation exception produces 500 status, but should be 400. Fix It!!!
    @Test
    void testRequestParamsNotValid() {
        client.get()
            .uri("/exchangerate?base=AA&to=BBB")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody()
            .jsonPath("$.message").isEqualTo("getLatest.base: length must be between 3 and 3");

        client.get()
            .uri("/exchangerate?base=AAA&to=BB")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody()
            .jsonPath("$.message").isEqualTo("getLatest.to: length must be between 3 and 3");
    }

    @Test
    public void testSuccess() {
        final String successUrl = "/exchangerate?base=AAA&to=BBB";
        final String externalServerUrl = "/latest?base=AAA&symbols=BBB";
        wireMockServer.stubFor(get(externalServerUrl)).setResponse(aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(HttpStatus.OK.value())
            .withBody(EXTERNAL_SUCCESS_RESPONSE)
            .build());
        client.get()
            .uri(successUrl)
            .exchange().expectStatus().isOk()
            .expectBody()
            .jsonPath("$.rate").isEqualTo(2);
    }
}
