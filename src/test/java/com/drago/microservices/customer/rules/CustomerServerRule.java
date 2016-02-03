package com.drago.microservices.customer.rules;


import com.drago.microservices.customer.CustomerServer;
import org.junit.rules.ExternalResource;

import javax.ws.rs.core.UriBuilder;

public class CustomerServerRule extends ExternalResource {

    static final String HOST = "localhost";
    static final String CUSTOMER_PATH = "customer";
    static final int PORT = 8090;

    private static final String SCHEME = "http";

    private CustomerServer server;


    @Override
    protected void before() throws Throwable {
        startAppServer();
    }

    @Override
    protected void after() {
        stopAppServer();
    }


    public UriBuilder baseUri() {
        return UriBuilder.fromPath(CUSTOMER_PATH)
                .host(HOST)
                .port(PORT)
                .scheme(SCHEME);
    }

    private void stopAppServer() {
        try {
            System.out.println("Stopping http server...");
            server.stop();
            System.out.println("HTTP server stopped...");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startAppServer() throws Exception {
        System.out.println("Starting http server...");
        server = new CustomerServer(HOST, PORT);
        server.start();
        System.out.println("HTTP server started at " + baseUri().build());
    }
}
