package com.sandeep.invoice.dto;

public enum Status {
    PENDING,
    PAID,
    VOID;

    public String nameLowerCase() {
        return name().toLowerCase();
    }
}
