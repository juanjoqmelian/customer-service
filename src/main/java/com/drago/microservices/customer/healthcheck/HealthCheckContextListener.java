package com.drago.microservices.customer.healthcheck;


import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

import javax.servlet.ServletContextEvent;

public class HealthCheckContextListener extends HealthCheckServlet.ContextListener {

    private final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();


    @Override
    public void contextInitialized(ServletContextEvent event) {

        healthCheckRegistry.register("customer-service", new CustomerServiceHealthCheck());
        super.contextInitialized(event);
    }

    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }
}
