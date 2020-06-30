package com.currencyexchange.server.model;

import javax.validation.constraints.DecimalMin;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Conversion response
 *
 * @author smykola
 */
@Data
@SuperBuilder
public class ConversionResponse extends ConversionRequest {
    @DecimalMin(value = "0", message = "Converted value should be greater than ")
    private Double converted;
}
