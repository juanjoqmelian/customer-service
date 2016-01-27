package com.drago.microservices;



import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CustomerResourceIT {

    private WebTarget webTarget;

    @ClassRule
    public static CustomerServerRule customerServerRule = new CustomerServerRule();


    @Before
    public void setUp() {

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);
        Client client = ClientBuilder.newClient(clientConfig);
        webTarget = client.target(customerServerRule.baseUri().build());
    }


    @Test
    public void shouldRetrieveAnExistingUserByAGivenId() {

        final String customerId = UUID.randomUUID().toString();

        Response response = webTarget.path(customerId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(Response.class);

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.getLink("self").getUri(), is(webTarget.getUriBuilder().path(customerId).build()));
        Customer customer = response.readEntity(Customer.class);
        assertThat(customer, is(new Customer(customerId, "Jon", 50000)));
    }

    @Test
    public void shouldCreateANewCustomer() {

        final Customer customer = new Customer("Chuck Norris", Integer.MAX_VALUE);

        Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(customer, MediaType.APPLICATION_JSON_TYPE), Response.class);

        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        assertThat(response.getLocation(), is(notNullValue()));
    }

    @Test
    public void shouldRetrieveAllExistingCustomers() {

        Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
                                    .get(Response.class);

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        //TODO - Improve the assertions of this test, shitty right now
        assertThat(response.getLinks(), hasSize(4));
        assertThat(response.getLink("self").getUri(), is(webTarget.getUriBuilder().build()));
        List<Customer> customers = response.readEntity(List.class);
        assertThat(customers, hasSize(3));
    }
}
