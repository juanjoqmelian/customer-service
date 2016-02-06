package com.drago.microservices.customer.domain;


import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
public class Customer {

    private String id;
    private String name;
    private int creditLimit;


    public Customer() {}

    public Customer(String name, int creditLimit) {
        this.name = name;
        this.creditLimit = creditLimit;
    }

    public Customer(String id, String name, int creditLimit) {
        this.id = id;
        this.name = name;
        this.creditLimit = creditLimit;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getCreditLimit() {
        return creditLimit;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(creditLimit, customer.creditLimit) &&
                Objects.equals(id, customer.id) &&
                Objects.equals(name, customer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, creditLimit);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", creditLimit=" + creditLimit +
                '}';
    }
}
