package com.sandeep.invoice.repository;

import com.sandeep.invoice.model.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    List<Invoice> findByStatusAndDueDateBefore(String status, LocalDate processingDate);
}
