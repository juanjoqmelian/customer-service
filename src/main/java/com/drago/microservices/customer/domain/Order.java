package com.drago.microservices.customer.domain;

import java.math.BigDecimal;
import java.util.Objects;


public class Order {

    private String id;
    private String customerId;
    private BigDecimal amount;
    private String itemId;
    private int quantity;


    public Order() {}

    public Order(String id, String customerId, BigDecimal amount, String itemId, int quantity) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(quantity, order.quantity) &&
                Objects.equals(id, order.id) &&
                Objects.equals(customerId, order.customerId) &&
                Objects.equals(amount, order.amount) &&
                Objects.equals(itemId, order.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, amount, itemId, quantity);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", amount=" + amount +
                ", itemId='" + itemId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
