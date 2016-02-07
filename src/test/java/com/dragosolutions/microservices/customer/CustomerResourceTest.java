package com.dragosolutions.microservices.customer;


import com.dragosolutions.microservices.customer.domain.Customer;
import com.dragosolutions.microservices.customer.exception.CustomerNotFoundException;
import com.dragosolutions.microservices.customer.repository.MongoCustomerRepository;
import com.google.common.collect.Lists;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CustomerResourceTest {

    private CustomerResource customerResource;

    private UriInfo mockUriInfo;
    private MongoCustomerRepository mockCustomerRepository;
    private Mockery mockery = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };


    @Before
    public void setUp() {

        mockUriInfo = mockery.mock(UriInfo.class);
        mockCustomerRepository = mockery.mock(MongoCustomerRepository.class);

        customerResource = new CustomerResource();
        customerResource.setMongoCustomerRepository(mockCustomerRepository);
        customerResource.setUriInfo(mockUriInfo);
    }


    @Test
    public void create_shouldCreateACustomerResource() {

        Customer customer = new Customer("Jon", 50000);

        mockery.checking(new Expectations() {
            {
                oneOf(mockUriInfo).getAbsolutePathBuilder();
                will(returnValue(UriBuilder.fromUri("http://example.com/customer")));

                oneOf(mockCustomerRepository).save(customer);
            }
        });

        Response response = customerResource.create(customer);

        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
    }

    @Test
    public void getCustomer_shouldReturnACustomerByAGivenId() {

        final Customer expectedCustomer = new Customer("123456", "Jon", 50000);
        final UriBuilder uriBuilder = UriBuilder.fromUri("http://example.com:8080");

        mockery.checking(new Expectations() {
            {
                atLeast(1).of(mockUriInfo).getBaseUriBuilder();
                will(returnValue(uriBuilder));

                oneOf(mockCustomerRepository).getCustomer(expectedCustomer.getId());
                will(returnValue(expectedCustomer));
            }
        });

        Response response = customerResource.getCustomer("123456");

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        Customer customer = (Customer) response.getEntity();
        assertThat(customer, is(expectedCustomer));
    }

    @Test(expected = CustomerNotFoundException.class)
    public void getCustomer_shouldReturnNotFoundIfCustomerDoesNotExist() {

        mockery.checking(new Expectations() {
            {
                oneOf(mockCustomerRepository).getCustomer("fakeCustomerId");
                will(throwException(new CustomerNotFoundException("Customer not found!")));
            }
        });

        customerResource.getCustomer("fakeCustomerId");
    }

    @Test
    public void getAll_shouldReturnAnEmptyListIfThereAreNoExistingCustomers() {

        final UriBuilder uriBuilder = UriBuilder.fromUri("http://example.com:8080");

        mockery.checking(new Expectations() {
            {
                oneOf(mockUriInfo).getBaseUriBuilder();
                will(returnValue(uriBuilder));

                oneOf(mockCustomerRepository).getAll();
                will(returnValue(Collections.emptyList()));
            }
        });

        Response response = customerResource.getAll();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        List<Customer> retrievedCustomers = (List<Customer>) response.getEntity();
        assertThat(retrievedCustomers, is(Collections.emptyList()));
    }

    @Test
    public void getAll_shouldReturnListOfExistingCustomers() {

        final UriBuilder uriBuilder = UriBuilder.fromUri("http://example.com:8080");
        final List<Customer> existingCustomers = Lists.newArrayList(
                new Customer(UUID.randomUUID().toString(), "Chuck", 33333),
                new Customer(UUID.randomUUID().toString(), "Jackie", 55555)
        );

        mockery.checking(new Expectations() {
            {
                exactly(3).of(mockUriInfo).getBaseUriBuilder();
                will(returnValue(uriBuilder));

                oneOf(mockCustomerRepository).getAll();
                will(returnValue(existingCustomers));
            }
        });

        Response response = customerResource.getAll();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        List<Customer> retrievedCustomers = (List<Customer>) response.getEntity();
        assertThat(retrievedCustomers, is(existingCustomers));
    }

    @Test
    public void update_shouldReturnNoContentIfCustomerIsUpdated() {

        final Customer customer = new Customer("123456", "Mary", 500000);
        final UriBuilder uriBuilder = UriBuilder.fromUri("http://example.com:8080");

        mockery.checking(new Expectations() {
            {
                oneOf(mockUriInfo).getBaseUriBuilder();
                will(returnValue(uriBuilder));

                oneOf(mockCustomerRepository).update(customer);
            }
        });

        Response response = customerResource.update(customer.getId(), customer);

        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test(expected = CustomerNotFoundException.class)
    public void update_shouldReturnNotFoundIfCustomerDoesNotExist() {

        final Customer customer = new Customer("fakeCustomerId", "Donatello", 32432);

        mockery.checking(new Expectations() {
            {
                oneOf(mockCustomerRepository).update(customer);
                will(throwException(new CustomerNotFoundException("Customer not found!")));
            }
        });

        customerResource.update(customer.getId(), customer);
    }

    @Test
    public void delete_shouldDeleteAnExistingCustomer() {

        String customerId = "123456";

        mockery.checking(new Expectations() {
            {
                oneOf(mockCustomerRepository).delete(customerId);
            }
        });

        Response response = customerResource.delete(customerId);

        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test(expected = CustomerNotFoundException.class)
    public void delete_shouldReturnNotFoundIfCustomerDoesNotExist() {

        mockery.checking(new Expectations() {
            {
                oneOf(mockCustomerRepository).delete("fakeCustomerId");
                will(throwException(new CustomerNotFoundException("Customer not found!")));
            }
        });

        Response response = customerResource.delete("fakeCustomerId");

        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void getOrders_shouldReturnEmptyCollectionIfCustomerHasNoOrders() {

    }


    @After
    public void tearDown() {
        mockery.assertIsSatisfied();
    }
}
