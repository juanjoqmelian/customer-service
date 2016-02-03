package com.drago.microservices.customer.repository;


public class CustomerRepositoryFactory {

    private static CustomerRepository customerRepository;


    public static CustomerRepository get(String host, int port) {
        if (customerRepository == null) {
            customerRepository = new CustomerRepository(host, port);
        }
        return customerRepository;
    }
}
