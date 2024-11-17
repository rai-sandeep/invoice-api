package com.sandeep.invoice.model;

import com.sandeep.invoice.dto.Status;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class Invoice {

    @Id
    Long id;

    BigDecimal amount;
    BigDecimal paidAmount;
    LocalDate dueDate;
    Status status;
}
