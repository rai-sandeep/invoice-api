package com.sandeep.invoice.repository;

import com.sandeep.invoice.model.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
}
