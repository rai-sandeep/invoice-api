package com.sandeep.invoice.service;

import com.sandeep.invoice.dto.*;
import com.sandeep.invoice.exception.InvoiceNotFoundException;
import com.sandeep.invoice.exception.InvoicePaymentDataException;
import com.sandeep.invoice.model.Invoice;
import com.sandeep.invoice.repository.InvoiceRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .status(Status.PENDING)
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

    /**
     * Processes a payment for a specified invoice.
     * <p>
     * This method allows payment to be made on an invoice if it has a PENDING status.
     * The payment amount is added to the current paid amount. If the total paid amount
     * equals the invoice amount, the invoice status is updated to PAID. The updated
     * invoice is saved in the repository.
     * </p>
     * <p>
     * The method performs the following validations:
     * <ul>
     *   <li>Throws an exception if the invoice with the specified {@code invoiceId} is not found.</li>
     *   <li>Throws an exception if the invoice is not in a PENDING status.</li>
     *   <li>Throws an exception if the payment amount exceeds the remaining balance of the invoice.</li>
     * </ul>
     * </p>
     *
     * @param invoiceId the ID of the invoice to be paid
     * @param amount    the amount to be paid towards the invoice
     * @return an {@link InvoiceResponse} object containing details of the updated invoice
     * @throws InvoiceNotFoundException if no invoice is found with the specified {@code invoiceId}
     * @throws InvoicePaymentDataException if the invoice is not in a PENDING status or if the payment
     *                                     amount exceeds the remaining balance
     */
    @Override
    public InvoiceResponse payInvoice(Long invoiceId, BigDecimal amount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(String.format(
                        "%s No invoice found with id %s", PAYMENT_FAILURE_MESSAGE_PREFIX, invoiceId)));

        if (!Status.PENDING.equals(invoice.getStatus())) {
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
            invoice.setStatus(Status.PAID);
        }

        invoiceRepository.save(invoice);

        return buildInvoiceResponse(invoice);
    }

    /**
     * Processes overdue invoices by updating their statuses and generating new invoices.
     * <p>
     * This method performs the following operations for invoices with a PENDING status
     * and a due date before the current date:
     * <ul>
     *   <li>If the invoice is partially paid, it marks the invoice as PAID and creates a
     *       new invoice for the remaining balance plus the late fee.</li>
     *   <li>If the invoice is not paid at all, it marks the invoice as VOID and creates a
     *       new invoice for the total amount plus the late fee.</li>
     * </ul>
     * The newly created invoices have a due date calculated by adding the number of overdue
     * days specified in the {@code request}.
     * </p>
     *
     * @param request the request object containing the late fee to be applied and the number
     *                of overdue days to calculate the new due date
     */
    @Override
    public void processOverdue(ProcessOverdueRequest request) {
        List<Invoice> invoices = invoiceRepository.findByStatusAndDueDateBefore(
                Status.PENDING, LocalDate.now());

        for (Invoice invoice : invoices) {
            Invoice newInvoice = Invoice.builder()
                    .amount(invoice.getAmount().subtract(invoice.getPaidAmount()).add(request.getLateFee()))
                    .paidAmount(BigDecimal.ZERO)
                    .dueDate(LocalDate.now().plusDays(request.getOverdueDays()))
                    .status(Status.PENDING)
                    .build();

            if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                invoice.setStatus(Status.PAID);
            } else {
                invoice.setStatus(Status.VOID);
            }

            invoiceRepository.saveAll(List.of(invoice, newInvoice));
        }
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
