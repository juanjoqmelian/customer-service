package com.drago.microservices.customer.repository;


import com.drago.microservices.customer.Customer;

import java.util.List;

public interface CustomerRepository {

    Customer getCustomer(String customerId);

    String save(Customer customer);

    void update(Customer customer);

    List<Customer> getAll();

    void delete(String customerId);
}
