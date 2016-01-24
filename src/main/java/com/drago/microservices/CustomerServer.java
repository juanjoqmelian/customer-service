package com.drago.microservices;


import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

public class CustomerServer {

    private final String host;
    private final int port;

    private HttpServer httpServer;


    public CustomerServer(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            this.httpServer = createHttpServer();
        } catch (IOException e) {
            System.out.println("Exception trying to start CustomerServer!");
        }
    }

    public void start() {
        httpServer.start();
    }


    public void stop() {
        httpServer.stop(0);
    }

    private HttpServer createHttpServer() throws IOException {

        ResourceConfig customerResourceConfig =
                new PackagesResourceConfig("com.drago.microservices");
        return HttpServerFactory.create(getCustomerResourceUri(), customerResourceConfig);
    }


    private URI getCustomerResourceUri() {
        return UriBuilder.fromUri("http://" + host + "/").port(port).build();
    }

    private String customerResourceHost() {
        String hostName = "localhost";
        try {
            hostName = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return hostName;
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Starting Customer Embedded Jersey HTTPServer...\n");

        final CustomerServer customerServer = new CustomerServer("localhost", 8085);
        customerServer.start();

        System.out.println(String.format("\nJersey Application Server started with WADL available at " + "%sapplication.wadl\n", customerServer.getCustomerResourceUri()));
        System.out.println("Started Customer Embedded Jersey HTTPServer Successfully !!!");
    }
}
