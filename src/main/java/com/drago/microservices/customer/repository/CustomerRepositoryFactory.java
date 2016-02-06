package com.drago.microservices.customer.repository;


import com.mongodb.MongoClient;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;

public class CustomerRepositoryFactory {

    private static CustomerRepository customerRepository;


    public static CustomerRepository get(String defaultHost, int defaultPort, String defaultDb) {
        if (customerRepository == null) {
            final String mongoHost = System.getenv("MONGO_HOST") != null ? System.getenv("MONGO_HOST") : defaultHost;
            final int mongoPort = System.getenv("MONGO_PORT") != null ? Integer.valueOf(System.getenv("MONGO_PORT")) : defaultPort;
            final String dbName = System.getenv("MONGO_DB") != null ? System.getenv("MONGO_DB") : defaultDb;

            MongoClient mongoClient = null;
            try {
                mongoClient = new MongoClient(mongoHost, mongoPort);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            final MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, dbName);
            customerRepository = new MongoCustomerRepository(mongoTemplate);
        }
        return customerRepository;
    }
}
