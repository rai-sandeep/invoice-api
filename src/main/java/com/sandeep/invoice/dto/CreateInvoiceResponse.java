package com.sandeep.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateInvoiceResponse {

    @NotBlank
    String id;
}
