package com.sandeep.invoice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class InvoiceResponse {
    String id;
    BigDecimal amount;
    @JsonProperty("paid_amount")
    BigDecimal paidAmount;
    @JsonProperty("due_date")
    LocalDate dueDate;
    Status status;
}
