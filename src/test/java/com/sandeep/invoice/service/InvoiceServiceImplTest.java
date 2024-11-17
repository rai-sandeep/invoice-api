package com.sandeep.invoice.service;

import com.sandeep.invoice.dto.*;
import com.sandeep.invoice.exception.InvoiceNotFoundException;
import com.sandeep.invoice.exception.InvoicePaymentDataException;
import com.sandeep.invoice.model.Invoice;
import com.sandeep.invoice.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Captor
    private ArgumentCaptor<List<Invoice>> invoiceCaptor;

    @Test
    void testCreateInvoice() {
        CreateInvoiceRequest request = CreateInvoiceRequest.builder()
                .amount(new BigDecimal("100.00"))
                .dueDate(LocalDate.of(2024, 11, 30))
                .build();

        Invoice savedInvoice = Invoice.builder()
                .id(1L)
                .amount(request.getAmount())
                .paidAmount(BigDecimal.ZERO)
                .dueDate(request.getDueDate())
                .status(Status.PENDING)
                .build();

        when(invoiceRepository.save(any(Invoice.class))).thenReturn(savedInvoice);

        CreateInvoiceResponse response = invoiceService.createInvoice(request);

        ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(captor.capture());

        Invoice capturedInvoice = captor.getValue();
        assertThat(capturedInvoice.getAmount()).isEqualTo(request.getAmount());
        assertThat(capturedInvoice.getDueDate()).isEqualTo(request.getDueDate());
        assertThat(capturedInvoice.getStatus()).isEqualTo(Status.PENDING);

        assertThat(response.getId()).isEqualTo("1");
    }

    @Test
    void testGetInvoices() {
        Invoice invoice1 = Invoice.builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(LocalDate.of(2024, 11, 30))
                .status(Status.PENDING)
                .build();

        Invoice invoice2 = Invoice.builder()
                .id(2L)
                .amount(new BigDecimal("200.00"))
                .paidAmount(new BigDecimal("50.00"))
                .dueDate(LocalDate.of(2024, 12, 10))
                .status(Status.PENDING)
                .build();

        when(invoiceRepository.findAll()).thenReturn(List.of(invoice1, invoice2));

        List<InvoiceResponse> invoices = invoiceService.getInvoices();

        assertThat(invoices).hasSize(2);
        assertThat(invoices.get(0).getId()).isEqualTo("1");
        assertThat(invoices.get(0).getStatus()).isEqualTo(Status.PENDING);
        assertThat(invoices.get(1).getAmount()).isEqualByComparingTo("200.00");
    }

    @Test
    void testPayInvoice_Success_PartAmount() {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(LocalDate.of(2024, 11, 30))
                .status(Status.PENDING)
                .build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        InvoiceResponse response = invoiceService.payInvoice(1L, new BigDecimal("50.00"));

        assertThat(response.getPaidAmount()).isEqualByComparingTo("50.00");
        assertThat(response.getStatus()).isEqualTo(Status.PENDING);
    }

    @Test
    void testPayInvoice_Success_FullAmount() {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .amount(new BigDecimal("50.00"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(LocalDate.of(2024, 11, 30))
                .status(Status.PENDING)
                .build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        InvoiceResponse response = invoiceService.payInvoice(1L, new BigDecimal("50.00"));

        assertThat(response.getPaidAmount()).isEqualByComparingTo("50.00");
        assertThat(response.getStatus()).isEqualTo(Status.PAID);
    }

    @Test
    void testPayInvoice_NotFound() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class,
                () -> invoiceService.payInvoice(1L, new BigDecimal("50.00")));
    }

    @Test
    void testPayInvoice_NotPending() {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .paidAmount(new BigDecimal("80.00"))
                .dueDate(LocalDate.of(2024, 11, 30))
                .status(Status.PAID)
                .build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(InvoicePaymentDataException.class,
                () -> invoiceService.payInvoice(1L, new BigDecimal("30.00")));
    }

    @Test
    void testPayInvoice_ExceedsAmount() {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .paidAmount(new BigDecimal("80.00"))
                .dueDate(LocalDate.of(2024, 11, 30))
                .status(Status.PENDING)
                .build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(InvoicePaymentDataException.class,
                () -> invoiceService.payInvoice(1L, new BigDecimal("30.00")));
    }

    @Test
    void testProcessOverdue() {
        Invoice overdueNotPaid = Invoice.builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(LocalDate.of(2024, 11, 1))
                .status(Status.PENDING)
                .build();
        Invoice overduePartPaid = Invoice.builder()
                .id(2L)
                .amount(new BigDecimal("100.00"))
                .paidAmount(new BigDecimal("20.00"))
                .dueDate(LocalDate.of(2024, 11, 1))
                .status(Status.PENDING)
                .build();

        ProcessOverdueRequest request = ProcessOverdueRequest.builder()
                .lateFee(new BigDecimal("10.00"))
                .overdueDays(30)
                .build();

        when(invoiceRepository.findByStatusAndDueDateBefore(eq(Status.PENDING), any(LocalDate.class)))
                .thenReturn(List.of(overdueNotPaid, overduePartPaid));

        invoiceService.processOverdue(request);

        verify(invoiceRepository, times(2)).saveAll(invoiceCaptor.capture());

        List<List<Invoice>> allCapturedInvoices = invoiceCaptor.getAllValues();
        assertThat(allCapturedInvoices).hasSize(2);

        List<Invoice> capturedInvoices = allCapturedInvoices.stream().flatMap(List::stream).toList();
        assertThat(capturedInvoices).hasSize(4);

        Invoice updatedInvoice = capturedInvoices.get(0);
        Invoice newInvoice = capturedInvoices.get(1);

        // Validate updated invoice
        assertThat(updatedInvoice.getId()).isEqualTo(1L);
        assertThat(updatedInvoice.getStatus()).isEqualTo(Status.VOID);

        // Validate new invoice
        assertThat(newInvoice.getAmount()).isEqualByComparingTo(new BigDecimal("110.00")); // Remaining + late fee
        assertThat(newInvoice.getPaidAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(newInvoice.getStatus()).isEqualTo(Status.PENDING);
        assertThat(newInvoice.getDueDate()).isEqualTo(LocalDate.now().plusDays(30));

        updatedInvoice = capturedInvoices.get(2);
        newInvoice = capturedInvoices.get(3);

        // Validate updated invoice
        assertThat(updatedInvoice.getId()).isEqualTo(2L);
        assertThat(updatedInvoice.getStatus()).isEqualTo(Status.PAID);

        // Validate new invoice
        assertThat(newInvoice.getAmount()).isEqualByComparingTo(new BigDecimal("90.00")); // Remaining + late fee
        assertThat(newInvoice.getPaidAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(newInvoice.getStatus()).isEqualTo(Status.PENDING);
        assertThat(newInvoice.getDueDate()).isEqualTo(LocalDate.now().plusDays(30));
    }
}
