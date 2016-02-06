package com.drago.microservices.customer.domain;

import java.math.BigDecimal;


public class Order {

    private String id;
    private final String customerId;
    private final BigDecimal quantity;


    public Order(String customerId, BigDecimal quantity, String id) {
        this.customerId = customerId;
        this.quantity = quantity;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
