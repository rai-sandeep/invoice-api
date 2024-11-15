package com.sandeep.invoice.service;

import com.sandeep.invoice.dto.CreateInvoiceRequest;
import com.sandeep.invoice.dto.CreateInvoiceResponse;
import jakarta.validation.constraints.NotNull;

public interface InvoiceService {
    CreateInvoiceResponse createInvoice(@NotNull CreateInvoiceRequest createInvoiceRequest);
}
