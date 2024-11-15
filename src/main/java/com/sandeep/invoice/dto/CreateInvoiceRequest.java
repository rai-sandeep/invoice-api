package com.sandeep.invoice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Date;

@Value
public class CreateInvoiceRequest {

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=7, fraction=2)
    @NotNull
    BigDecimal amount;

    @JsonProperty("due_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    Date dueDate;
}
