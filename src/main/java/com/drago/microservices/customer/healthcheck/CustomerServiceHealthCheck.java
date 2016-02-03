package com.drago.microservices.customer.healthcheck;

import com.codahale.metrics.health.HealthCheck;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;


public class CustomerServiceHealthCheck extends HealthCheck {

    private final Client client;


    public CustomerServiceHealthCheck() {
        this.client = ClientBuilder.newClient();
    }


    @Override
    protected Result check() throws Exception {

        return Result.healthy();
    }
}
