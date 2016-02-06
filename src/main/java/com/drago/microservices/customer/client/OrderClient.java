package com.drago.microservices.customer.client;


import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class OrderClient {

    private static final String ORDERS_REL = "orders";

    private WebTarget target;

    public OrderClient(String host, int port) {
        ClientConfig clientConfig = new ClientConfig();
//        clientConfig.register(JacksonFeature.class);
        Client client = ClientBuilder.newClient(clientConfig);
        target = client.target(String.format("http://%s:%d", host, port));
    }

    public Response getOrder(final String orderId) {
        Response response = target.request().get();
        if (response.getStatusInfo().equals(Response.Status.NOT_FOUND)) {
            throw new ServiceUnavailableException("Customer service does not seem to be available!");
        }
        final String customerUri = response.getLink(ORDERS_REL).toString();
        return target.path(customerUri)
                .path(orderId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
    }
}
