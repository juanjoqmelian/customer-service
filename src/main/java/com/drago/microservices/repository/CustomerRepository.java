package com.drago.microservices.repository;


import com.drago.microservices.Customer;
import com.drago.microservices.exception.CustomerNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CustomerRepository {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> syncCommands;
    private ObjectMapper objectMapper= new ObjectMapper();


    CustomerRepository(String host, int port) {
        redisClient = RedisClient.create(String.format("redis://%s:%d/0", host, port));
        connection = redisClient.connect();
        syncCommands = connection.sync();
    }


    public Customer getCustomer(String customerId) {

        Customer customer = null;

        try {
            final String storedValue = syncCommands.get(customerId);
            if (Strings.isNullOrEmpty(storedValue)) {
                throw new CustomerNotFoundException("Customer with id=" + customerId + " does not exist!");
            }
            customer = objectMapper.readValue(storedValue, Customer.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customer;
    }

    public String save(Customer customer) {
        try {
            if (Strings.isNullOrEmpty(customer.getId())) {
                customer.setId(UUID.randomUUID().toString());
            }
            syncCommands.set(customer.getId(), objectMapper.writeValueAsString(customer));
            return customer.getId();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public void update(Customer customer) {
        try {
            String storedValue = syncCommands.get(customer.getId());
            if (Strings.isNullOrEmpty(storedValue)) {
                throw new CustomerNotFoundException("Customer with id=" + customer.getId() + " does not exist!");
            }
            syncCommands.set(customer.getId(), objectMapper.writeValueAsString(customer));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Customer> getAll() {
        return Collections.emptyList();
    }

    public void delete(String customerId) {
        Customer customer = getCustomer(customerId);
        if (customer != null) syncCommands.del(customerId);
    }
}
