package com.sandeep.invoice.controller;

import com.sandeep.invoice.dto.*;
import com.sandeep.invoice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(description = "Processes a payment for a specified invoice. If the invoice is fully paid, it is marked as PAID.")
    public InvoiceResponse payInvoice(@PathVariable @NotNull @Positive Long invoiceId,
            @Valid @RequestBody PayInvoiceRequest payInvoiceRequest) {
        return invoiceService.payInvoice(invoiceId, payInvoiceRequest.getAmount());
    }

    @PostMapping("process-overdue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Processes overdue invoices by handling partially and fully unpaid invoices. " +
            "Partially paid invoices are marked as PAID with a new invoice created for the remaining balance plus late fees. " +
            "Fully unpaid invoices are marked as VOID, with a new invoice created for the total amount plus late fees.")
    public void processOverdue(@Valid @RequestBody ProcessOverdueRequest request) {
        invoiceService.processOverdue(request);
    }
}
