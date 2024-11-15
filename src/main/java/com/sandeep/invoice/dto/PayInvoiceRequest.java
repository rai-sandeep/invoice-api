package com.sandeep.invoice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class PayInvoiceRequest {

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=7, fraction=2)
    BigDecimal amount;
}
