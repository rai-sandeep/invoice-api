package com.sandeep.invoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
public class CreateInvoiceRequest {

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=7, fraction=2)
    @NotNull
    BigDecimal amount;

    @JsonProperty("due_date")
    @NotNull
    LocalDate dueDate;
}
