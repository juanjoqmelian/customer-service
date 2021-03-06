package com.dragosolutions.microservices.customer.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
public class CreditLog {

    private String customerId;
    private String orderId;
    private BigDecimal amount;

    public CreditLog() {
    }

    public CreditLog(String customerId, String orderId, BigDecimal amount) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
