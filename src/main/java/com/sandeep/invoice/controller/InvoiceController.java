package com.sandeep.invoice.controller;

import com.sandeep.invoice.dto.CreateInvoiceRequest;
import com.sandeep.invoice.dto.CreateInvoiceResponse;
import com.sandeep.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateInvoiceResponse createInvoice(
            @Valid @RequestBody CreateInvoiceRequest createInvoiceRequest) {
        return invoiceService.createInvoice(createInvoiceRequest);
    }
}
