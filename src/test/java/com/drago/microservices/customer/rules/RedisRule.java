package com.drago.microservices.customer.rules;


import com.drago.microservices.customer.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import org.junit.rules.ExternalResource;
import redis.embedded.RedisServer;

import java.io.IOException;

public class RedisRule extends ExternalResource {

    private final String host;
    private final int port;

    private static RedisServer redisServer;
    private static RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private static RedisCommands<String, String> syncCommands;
    private static ObjectMapper objectMapper= new ObjectMapper();


    public RedisRule(String host, int port) {
        this.host = host;
        this.port = port;
    }


    @Override
    protected void before() throws Throwable {
        redisServer = new RedisServer(port);
        redisServer.start();
        System.out.println("Redis has been started...");
        redisClient = RedisClient.create(String.format("redis://%s:%d/0", host, port));
        connection = redisClient.connect();
        syncCommands = connection.sync();
    }


    public void insert(Customer customer) throws JsonProcessingException {
        syncCommands.set(customer.getId(), objectMapper.writeValueAsString(customer));
    }

    public Customer getCustomer(String customerId) throws IOException {
        String storedValue = syncCommands.get(customerId);
        if (storedValue == null) return null;
        Customer customer = objectMapper.readValue(storedValue, Customer.class);
        return customer;
    }

    public void cleanup() {
        syncCommands.flushdb();
    }

    @Override
    protected void after() {
        connection.close();
        redisClient.shutdown();
        try {
            redisServer.stop();
            System.out.println("Redis has been stopped...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
