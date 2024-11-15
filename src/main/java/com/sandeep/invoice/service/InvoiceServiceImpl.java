package com.sandeep.invoice.service;

import com.sandeep.invoice.dto.*;
import com.sandeep.invoice.exception.InvoiceNotFoundException;
import com.sandeep.invoice.exception.InvoicePaymentDataException;
import com.sandeep.invoice.model.Invoice;
import com.sandeep.invoice.repository.InvoiceRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final String PAYMENT_FAILURE_MESSAGE_PREFIX = "Payment failed: ";

    private final InvoiceRepository invoiceRepository;

    @Override
    public CreateInvoiceResponse createInvoice(@NotNull CreateInvoiceRequest createInvoiceRequest) {
        Invoice invoice = Invoice.builder()
                .amount(createInvoiceRequest.getAmount())
                .paidAmount(BigDecimal.ZERO)
                .dueDate(createInvoiceRequest.getDueDate())
                .status(Status.PENDING.nameLowerCase())
                .build();

        invoice = invoiceRepository.save(invoice);

        return CreateInvoiceResponse.builder()
                .id(String.valueOf(invoice.getId()))
                .build();
    }

    @Override
    public List<InvoiceResponse> getInvoices() {
        List<InvoiceResponse> responseList = new ArrayList<>();
        invoiceRepository.findAll().forEach(invoice ->
                responseList.add(buildInvoiceResponse(invoice)));
        return responseList;
    }

    @Override
    public InvoiceResponse payInvoice(Long invoiceId, BigDecimal amount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(String.format(
                        "%s No invoice found with id %s", PAYMENT_FAILURE_MESSAGE_PREFIX, invoiceId)));

        if (!Status.PENDING.nameLowerCase().equals(invoice.getStatus())) {
            throw new InvoicePaymentDataException(String.format(
                    "%s Payments can be made only for pending invoices. Status of invoice id %s is: %s",
                    PAYMENT_FAILURE_MESSAGE_PREFIX, invoiceId, invoice.getStatus()));
        }

        BigDecimal newPaidAmount = invoice.getPaidAmount().add(amount);

        if (newPaidAmount.compareTo(invoice.getAmount()) > 0) {
            throw new InvoicePaymentDataException(String.format(
                    "%s Payment amount %s is more than remaining payment amount %s",
                    PAYMENT_FAILURE_MESSAGE_PREFIX, amount, invoice.getAmount().subtract(invoice.getPaidAmount())));
        }

        invoice.setPaidAmount(newPaidAmount);
        if (newPaidAmount.equals(invoice.getAmount())) {
            invoice.setStatus(Status.PAID.nameLowerCase());
        }

        invoiceRepository.save(invoice);

        return buildInvoiceResponse(invoice);
    }

    @Override
    public void processOverdue(ProcessOverdueRequest request) {
        invoiceRepository.findByStatusAndDueDateBefore(
                Status.PENDING.nameLowerCase(), LocalDate.now()).forEach(
                        invoice -> processOverdue(request, invoice));
    }

    @Transactional
    public void processOverdue(ProcessOverdueRequest request, Invoice invoice) {
        Invoice newInvoice = Invoice.builder()
                .amount(invoice.getAmount().subtract(invoice.getPaidAmount()).add(request.getLateFee()))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(LocalDate.now().plusDays(request.getOverdueDays()))
                .status(Status.PENDING.nameLowerCase())
                .build();

        if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(Status.PAID.nameLowerCase());
        } else {
            invoice.setStatus(Status.VOID.nameLowerCase());
        }

        invoiceRepository.save(invoice);
        invoiceRepository.save(newInvoice);
    }

    private InvoiceResponse buildInvoiceResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(String.valueOf(invoice.getId()))
                .amount(invoice.getAmount())
                .paidAmount(invoice.getPaidAmount())
                .dueDate(invoice.getDueDate())
                .status(invoice.getStatus())
                .build();
    }
}
