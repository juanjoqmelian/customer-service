package com.drago.microservices;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.when;

public class CustomerResourceTest {

    private CustomerResource customerResource = new CustomerResource();

    private UriInfo mockUriInfo;


    @Before
    public void setUp() {

        mockUriInfo = Mockito.mock(UriInfo.class);
        customerResource.setUriInfo(mockUriInfo);

        final UriBuilder uriBuilder = UriBuilder.fromUri("http://example.com:8080");
        when(mockUriInfo.getBaseUri()).thenReturn(uriBuilder.build());
        when(mockUriInfo.getBaseUriBuilder()).thenReturn(uriBuilder);
    }


    @Test
    public void create_shouldCreateACustomerResource() {

        when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(UriBuilder.fromUri("http://example.com/customer"));
        Customer customer = new Customer("Jon", 50000);

        Response response = customerResource.create(customer);

        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
    }

    @Test
    public void getCustomer_shouldReturnACustomerByAGivenId() {

        Customer expectedCustomer = new Customer("123456", "Jon", 50000);

        Response response = customerResource.getCustomer("123456");

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        Customer customer = (Customer) response.getEntity();
        assertThat(customer, is(expectedCustomer));
    }

    @Test
    public void getAll_shouldReturnListOfExistingCustomers() {

        Response response = customerResource.getAll();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        List<Customer> existingCustomers = (List<Customer>) response.getEntity();
        assertThat(existingCustomers, is(not(empty())));
    }

    @Test
    public void update_shouldReturnNoContentIfCustomerIsUpdated() {

        final Customer customer = new Customer("123456", "Mary", 500000);

        Response response = customerResource.update(customer.getId(), customer);

        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Ignore("WIP")
    @Test
    public void update_shouldReturnNotFoundIfCustomerDoesNotExist() {

        final Customer customer = new Customer("fakeCustomerId", "Donatello", 32432);

        Response response = customerResource.update(customer.getId(), customer);

        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void delete_shouldDeleteAnExistingCustomer() {

        String customerId = "123456";

        Response response = customerResource.delete(customerId);

        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Ignore("WIP")
    @Test
    public void delete_shouldReturnNotFoundIfCustomerDoesNotExist() {

        Response response = customerResource.delete("fakeCustomerId");

        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }
}
