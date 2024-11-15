package com.sandeep.invoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class ProcessOverdueRequest {

    @JsonProperty("late_fee")
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=7, fraction=2)
    @NotNull
    BigDecimal lateFee;

    @JsonProperty("overdue_days")
    @Min(1)
    int overdueDays;
}
