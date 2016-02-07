package com.drago.microservices.customer.domain;

import java.math.BigDecimal;
import java.util.Objects;


public class Order {

    private String id;
    private String customerId;
    private BigDecimal quantity;

    public Order() {}

    public Order(String id, String customerId, BigDecimal quantity) {
        this.id = id;
        this.customerId = customerId;
        this.quantity = quantity;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) &&
                Objects.equals(customerId, order.customerId) &&
                Objects.equals(quantity, order.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, quantity);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
