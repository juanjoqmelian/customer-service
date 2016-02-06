package com.drago.microservices.customer.config;

import com.drago.microservices.customer.repository.CustomerRepository;
import com.drago.microservices.customer.repository.MongoCustomerRepository;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;

@Configuration
@ComponentScan(value = {
        "com.drago.microservices.customer", "com.drago.microservices.customer.repository"
})
public class SpringConfiguration {

    @Bean
    public CustomerRepository getMongoCustomerRepository() {

        final String mongoHost = System.getenv("MONGO_HOST") != null ? System.getenv("MONGO_HOST") : "localhost";
        final int mongoPort = System.getenv("MONGO_PORT") != null ? Integer.valueOf(System.getenv("MONGO_PORT")) : 27017;

        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(mongoHost, mongoPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        final MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "customer");
        final MongoCustomerRepository mongoCustomerRepository = new MongoCustomerRepository(mongoTemplate);
        return mongoCustomerRepository;
    }

}
