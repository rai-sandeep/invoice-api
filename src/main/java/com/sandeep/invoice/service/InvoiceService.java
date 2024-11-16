package com.sandeep.invoice.service;

import com.sandeep.invoice.dto.CreateInvoiceRequest;
import com.sandeep.invoice.dto.CreateInvoiceResponse;
import com.sandeep.invoice.dto.InvoiceResponse;
import com.sandeep.invoice.dto.ProcessOverdueRequest;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {
    CreateInvoiceResponse createInvoice(@NotNull CreateInvoiceRequest createInvoiceRequest);
    List<InvoiceResponse> getInvoices();
    InvoiceResponse payInvoice(@NotNull Long invoiceId, @NotNull BigDecimal amount);
    void processOverdue(@NotNull ProcessOverdueRequest processOverdueRequest);
}
