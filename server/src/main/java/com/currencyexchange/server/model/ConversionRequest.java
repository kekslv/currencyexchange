package com.currencyexchange.server.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Conversion request
 *
 * @author smykola
 */
@Data
@AllArgsConstructor
@SuperBuilder
public class ConversionRequest {

    @NotBlank
    @Size(min = 3, max = 3)
    private String from;

    @NotBlank
    @Size(min = 3, max = 3)
    private String to;

    @DecimalMin(value = "0")
    @NotNull
    private Double amount;
}
