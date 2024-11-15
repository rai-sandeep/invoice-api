package com.sandeep.invoice.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Date;

@Value
public class InvoiceResponse {
    String id;
    BigDecimal amount;
    BigDecimal paidAmount;
    Date dueDate;
    Status status;
}
