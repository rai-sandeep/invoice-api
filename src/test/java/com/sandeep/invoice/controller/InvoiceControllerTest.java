package com.sandeep.invoice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandeep.invoice.dto.*;
import com.sandeep.invoice.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateInvoice() throws Exception {
        CreateInvoiceRequest request = CreateInvoiceRequest.builder()
                .amount(new BigDecimal("100.00"))
                .dueDate(LocalDate.parse("2024-11-30"))
                .build();

        CreateInvoiceResponse response = CreateInvoiceResponse.builder()
                .id("1")
                .build();
        when(invoiceService.createInvoice(any(CreateInvoiceRequest.class))).thenReturn(response);

        mockMvc.perform(post("/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")));
    }

    @Test
    void testGetInvoices() throws Exception {
        InvoiceResponse invoice = InvoiceResponse.builder()
                .id("1")
                .amount(new BigDecimal("100.00"))
                .paidAmount(new BigDecimal("50.00"))
                .dueDate(LocalDate.parse("2024-11-30"))
                .status(Status.PENDING)
                .build();
        when(invoiceService.getInvoices()).thenReturn(List.of(invoice));

        mockMvc.perform(get("/invoices")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].amount", is(100.00)))
                .andExpect(jsonPath("$[0].dueDate", is("2024-11-30")))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }

    @Test
    void testPayInvoice() throws Exception {
        PayInvoiceRequest request = new PayInvoiceRequest();
        request.setAmount(new BigDecimal("50.00"));

        InvoiceResponse response = InvoiceResponse.builder()
                .id("1")
                .amount(new BigDecimal("100.00"))
                .paidAmount(new BigDecimal("50.00"))
                .dueDate(LocalDate.parse("2024-11-30"))
                .status(Status.PENDING)
                .build();
        when(invoiceService.payInvoice(eq(1L), eq(new BigDecimal("50.00")))).thenReturn(response);

        mockMvc.perform(post("/invoices/1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    void testProcessOverdue() throws Exception {
        ProcessOverdueRequest request = ProcessOverdueRequest.builder()
                .lateFee(new BigDecimal("10.00"))
                .overdueDays(30)
                .build();

        doNothing().when(invoiceService).processOverdue(any(ProcessOverdueRequest.class));

        mockMvc.perform(post("/invoices/process-overdue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testValidationFailureForCreateInvoice() throws Exception {
        CreateInvoiceRequest request = CreateInvoiceRequest.builder().build();

        mockMvc.perform(post("/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Validation failed")));
    }

    @Test
    void testValidationFailureForPayInvoice() throws Exception {
        PayInvoiceRequest request = new PayInvoiceRequest();
        request.setAmount(new BigDecimal("-50.00"));

        mockMvc.perform(post("/invoices/1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Validation failed")));
    }
}
