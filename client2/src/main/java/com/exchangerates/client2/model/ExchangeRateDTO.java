package com.exchangerates.client2.model;

import java.time.LocalDate;
import java.util.Map;

import lombok.Data;

/**
 * A response object from external service
 *
 * @author smykola
 */
@Data
public class ExchangeRateDTO {
    private Map<String, Double> rates;
    private String base;
    private LocalDate date;
}
