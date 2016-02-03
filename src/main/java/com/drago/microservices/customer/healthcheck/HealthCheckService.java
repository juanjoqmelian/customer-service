package com.drago.microservices.customer.healthcheck;


import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class HealthCheckService extends Application<HealthCheckConfiguration> {

    public static void main(String[] args) throws Exception {
        new HealthCheckService().run(args);
    }

    @Override
    public void run(HealthCheckConfiguration healthCheckConfiguration, Environment environment) throws Exception {
        environment.healthChecks().register("customer-service", new CustomerServiceHealthCheck());
    }
}
