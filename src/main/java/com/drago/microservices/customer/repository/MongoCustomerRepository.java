package com.drago.microservices.customer.repository;


import com.drago.microservices.customer.domain.Customer;
import com.drago.microservices.customer.exception.CustomerNotFoundException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class MongoCustomerRepository implements CustomerRepository {

    private MongoTemplate mongoTemplate;


    public MongoCustomerRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Customer getCustomer(String customerId) {

        Customer customer = mongoTemplate.findOne(Query.query(Criteria.where("id").is(customerId)), Customer.class);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with id='" + customerId + "' does not exist!");
        }
        return customer;
    }

    @Override
    public String save(Customer customer) {
        mongoTemplate.save(customer);
        return customer.getId();
    }

    @Override
    public void update(Customer customer) {

        if (!mongoTemplate.exists(Query.query(Criteria.where("id").is(customer.getId())), Customer.class)) {
            throw new CustomerNotFoundException("Customer with id='" + customer.getId() + "' does not exist!");
        }

        mongoTemplate.save(customer);
    }

    @Override
    public List<Customer> getAll() {
        return mongoTemplate.findAll(Customer.class);
    }

    @Override
    public void delete(String customerId) {
        final Customer customer = this.getCustomer(customerId);
        mongoTemplate.remove(customer);
    }
}
