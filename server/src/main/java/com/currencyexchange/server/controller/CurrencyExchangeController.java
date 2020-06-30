package com.currencyexchange.server.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.currencyexchange.server.model.ConversionRequest;
import com.currencyexchange.server.model.ConversionResponse;
import com.currencyexchange.server.service.CurrencyExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * @author smykola
 */
@RestController
@AllArgsConstructor
public class CurrencyExchangeController {

    private CurrencyExchangeService currencyExchangeService;

    @Operation(summary = "Exchanges currencies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currencies exchanged successfully", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConversionResponse.class))}),
        @ApiResponse(responseCode = "503", description = "No providers available", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseStatusException.class))}),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseStatusException.class))})
    })
    @PostMapping("/currency/convert")
    public Mono<ConversionResponse> exchange(@Valid @RequestBody ConversionRequest conversionRequest) {
        return currencyExchangeService.exchange(conversionRequest).switchIfEmpty(Mono.error(new ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE, "No providers available")));
    }

}
