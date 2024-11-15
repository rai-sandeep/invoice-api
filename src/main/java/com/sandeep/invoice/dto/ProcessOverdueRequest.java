package com.sandeep.invoice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class ProcessOverdueRequest {

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=7, fraction=2)
    BigDecimal late_fee;

    @Min(1)
    int overdueDays;
}
