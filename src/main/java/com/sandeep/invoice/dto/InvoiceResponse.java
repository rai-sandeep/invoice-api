package com.sandeep.invoice.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class InvoiceResponse {
    String id;
    BigDecimal amount;
    BigDecimal paidAmount;
    LocalDate dueDate;
    Status status;
}
