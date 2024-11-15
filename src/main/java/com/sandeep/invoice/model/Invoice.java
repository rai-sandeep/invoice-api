package com.sandeep.invoice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class Invoice {

    @Id
    Long id;

    BigDecimal amount;
    BigDecimal paidAmount;
    Date dueDate;
    String status;
}
