package com.sandeep.invoice.controller;

import com.sandeep.invoice.dto.*;
import com.sandeep.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@Validated
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateInvoiceResponse createInvoice(
            @Valid @RequestBody CreateInvoiceRequest createInvoiceRequest) {
        return invoiceService.createInvoice(createInvoiceRequest);
    }

    @GetMapping
    public List<InvoiceResponse> getInvoices() {
        return invoiceService.getInvoices();
    }

    @PostMapping("{invoiceId}/payments")
    public InvoiceResponse payInvoice(@PathVariable @NotNull @Positive Long invoiceId,
            @Valid @RequestBody PayInvoiceRequest payInvoiceRequest) {
        return invoiceService.payInvoice(invoiceId, payInvoiceRequest.getAmount());
    }

    @PostMapping("process-overdue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void processOverdue(@Valid @RequestBody ProcessOverdueRequest request) {
        invoiceService.processOverdue(request);
    }
}
