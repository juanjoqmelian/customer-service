package com.drago.microservices;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CustomerResourceIT {

    private WebResource webResource;

    @ClassRule
    public static CustomerServerRule customerServerRule = new CustomerServerRule();


    @Before
    public void setUp() {

        Client client = Client.create();
        webResource = client.resource(customerServerRule.baseUri().build());
    }


    @Test
    public void shouldRetrieveAnExistingUserByAGivenId() {

        final String customerId = UUID.randomUUID().toString();

        ClientResponse response = webResource.path(customerId)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(ClientResponse.Status.OK.getStatusCode()));
        Customer customer = response.getEntity(Customer.class);
        assertThat(customer, is(new Customer(customerId, "Jon", 50000)));
    }

    @Test
    public void shouldCreateANewCustomer() {

        final Customer customer = new Customer("Chuck Norris", Integer.MAX_VALUE);

        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, customer);

        assertThat(response.getStatus(), is(ClientResponse.Status.CREATED.getStatusCode()));
        assertThat(response.getLocation(), is(notNullValue()));
    }
}
