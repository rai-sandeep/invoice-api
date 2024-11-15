package com.sandeep.invoice.service;

import com.sandeep.invoice.dto.CreateInvoiceRequest;
import com.sandeep.invoice.dto.CreateInvoiceResponse;
import com.sandeep.invoice.dto.Status;
import com.sandeep.invoice.model.Invoice;
import com.sandeep.invoice.repository.InvoiceRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    public CreateInvoiceResponse createInvoice(@NotNull CreateInvoiceRequest createInvoiceRequest) {
        Invoice invoice = buildInvoice(createInvoiceRequest);

        invoice = invoiceRepository.save(invoice);

        return CreateInvoiceResponse.builder()
                .id(String.valueOf(invoice.getId()))
                .build();
    }

    private Invoice buildInvoice(CreateInvoiceRequest createInvoiceRequest) {
        return Invoice.builder()
                .amount(createInvoiceRequest.getAmount())
                .paidAmount(BigDecimal.ZERO)
                .dueDate(createInvoiceRequest.getDueDate())
                .status(Status.PENDING.name().toLowerCase())
                .build();
    }
}
