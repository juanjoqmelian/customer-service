package com.drago.microservices.customer;



import com.drago.microservices.customer.domain.Customer;
import com.drago.microservices.customer.domain.Order;
import com.drago.microservices.customer.rules.CustomerServerRule;
import com.drago.microservices.customer.rules.MongoRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class CustomerResourceIT {

    private WebTarget webTarget;


    @ClassRule
    public static CustomerServerRule customerServerRule = new CustomerServerRule();

    @ClassRule
    public static MongoRule mongoRule = new MongoRule(Customer.class);


    @Before
    public void setUp() {

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);
        Client client = ClientBuilder.newClient(clientConfig);
        webTarget = client.target(customerServerRule.baseUri().build());
        mongoRule.dropCollections();
    }


    @Test
    public void getCustomer_shouldReturnBadRequestIfCustomerDoesNotExist() {

        final String fakeCustomerId = "fake-customer-id";

        Response response = webTarget.path(fakeCustomerId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(Response.class);

        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void getCustomer_shouldRetrieveAnExistingUserByAGivenId() throws JsonProcessingException {

        final String customerId = UUID.randomUUID().toString();
        final Customer existingCustomer = new Customer(customerId, "Chuck Norris", 9000);
        mongoRule.insert(existingCustomer);

        Response response = webTarget.path(customerId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(Response.class);

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.getLink("self").getUri(), is(webTarget.getUriBuilder().path(customerId).build()));
        Customer customer = response.readEntity(Customer.class);
        assertThat(customer, is(new Customer(customerId, "Chuck Norris", 9000)));
    }

    @Test
    public void create_shouldCreateANewCustomer() {

        final Customer customer = new Customer("Chuck Norris", Integer.MAX_VALUE);

        Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(customer, MediaType.APPLICATION_JSON_TYPE), Response.class);

        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        assertThat(response.getLocation(), is(notNullValue()));
    }

    @Test
    public void update_shouldUpdateAnExistingCustomer() throws IOException {

        final String customerId = UUID.randomUUID().toString();
        final Customer existingCustomer = new Customer(customerId, "Chuck Norris", 9000);
        mongoRule.insert(existingCustomer);
        final Customer updatedCustomer = new Customer(customerId, "Jean Claude", 10000);

        Response response = webTarget.path(customerId).request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(updatedCustomer, MediaType.APPLICATION_JSON_TYPE), Response.class);

        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        Customer retrievedCustomer = mongoRule.findOne(Query.query(Criteria.where("id").is(customerId)), Customer.class);
        assertThat(retrievedCustomer, is(updatedCustomer));
    }

    @Test
    public void update_shouldReturnBadRequestIfCustomerDoesNotExist() throws IOException {

        final Customer updatedCustomer = new Customer("fake-id", "Jean Claude", 10000);

        Response response = webTarget.path(updatedCustomer.getId()).request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(updatedCustomer, MediaType.APPLICATION_JSON_TYPE), Response.class);

        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void delete_shouldDeleteAnExistingCustomer() throws IOException {

        final String customerId = UUID.randomUUID().toString();
        final Customer existingCustomer = new Customer(customerId, "Chuck Norris", 9000);
        mongoRule.insert(existingCustomer);

        Response response = webTarget.path(customerId).request(MediaType.APPLICATION_JSON_TYPE)
                .delete(Response.class);

        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
        Customer retrievedCustomer = mongoRule.findOne(Query.query(Criteria.where("id").is(customerId)), Customer.class);
        assertThat(retrievedCustomer, is(nullValue()));
    }

    @Test
    public void getAll_shouldRetrieveAllExistingCustomers() {

        final Customer firstCustomer = new Customer(UUID.randomUUID().toString(), "Chuck Norris", 9000);
        mongoRule.insert(firstCustomer);
        final Customer secondCustomer = new Customer(UUID.randomUUID().toString(), "Steven Seagal", 12000);
        mongoRule.insert(secondCustomer);

        Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
                                    .get(Response.class);

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        //TODO - Improve the assertions of this test, shitty right now
        assertThat(response.getLinks(), hasSize(3));
        assertThat(response.getLink("self").getUri(), is(webTarget.getUriBuilder().build()));
        List<Customer> customers = response.readEntity(List.class);
        assertThat(customers, hasSize(2));
    }

    @Test
    public void getOrders_shouldReturnEmptyCollectionIfCustomerHasNoExistingOrders() {

        final Customer customer = new Customer(UUID.randomUUID().toString(), "Chuck Norris", 9000);
        mongoRule.insert(customer);

        Response response = webTarget.path(customer.getId())
                .path("/orders")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(Response.class);

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.getLinks(), hasSize(1));
        assertThat(response.getLink("self").getUri(), is(webTarget.getUriBuilder().path(customer.getId()).path("/orders").build()));
        List<Order> orders = response.readEntity(List.class);
        assertThat(orders, is(empty()));
    }
}
