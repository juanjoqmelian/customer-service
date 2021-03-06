package com.dragosolutions.microservices.customer;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jersey2.InstrumentedResourceMethodApplicationListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class CustomerServer {

    private final String host;
    private final int port;

    private Server httpServer;


    public CustomerServer(String host, int port) {
        this.host = host;
        this.port = port;

        this.httpServer = createHttpServer();
    }

    public void start() {
        try {

            httpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stop() {
        try {
            httpServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private URI getCustomerResourceUri() {
        return UriBuilder.fromUri("http://" + host + "/").port(port).build();
    }

    private Server createHttpServer() {
        ResourceConfig resourceConfig = initialiseResourceConfig();
        ServletContextHandler context = initialiseContext(resourceConfig);
        Server server = new Server(port);
        server.setHandler(context);
        return server;
    }

    private ServletContextHandler initialiseContext(ResourceConfig resourceConfig) {
        ServletContainer servletContainer = new ServletContainer(resourceConfig);
        ServletHolder servletHolder = new ServletHolder(servletContainer);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(servletHolder, "/*");
        return context;
    }

    private ResourceConfig initialiseResourceConfig() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages(CustomerResource.class.getPackage().getName());
        resourceConfig.register(new InstrumentedResourceMethodApplicationListener(new MetricRegistry()));
        resourceConfig.register(JacksonFeature.class);
        return resourceConfig;
    }


    public static void main(String[] args) throws IOException {

        System.out.println("Starting Customer Embedded Jersey HTTPServer...\n");

        final CustomerServer customerServer = new CustomerServer("localhost", 8085);
        customerServer.start();

        System.out.println(String.format("\nJersey Application Server started with WADL available at " + "%sapplication.wadl\n", customerServer.getCustomerResourceUri()));
        System.out.println("Started Customer Embedded Jersey HTTPServer Successfully !!!");
    }
}
