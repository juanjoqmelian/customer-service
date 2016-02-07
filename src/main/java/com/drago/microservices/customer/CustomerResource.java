package com.drago.microservices.customer;

import com.codahale.metrics.annotation.Timed;
import com.drago.microservices.customer.client.OrderClient;
import com.drago.microservices.customer.domain.CreditLog;
import com.drago.microservices.customer.domain.Customer;
import com.drago.microservices.customer.domain.Order;
import com.drago.microservices.customer.repository.CreditLogRepository;
import com.drago.microservices.customer.repository.CustomerRepository;
import com.drago.microservices.customer.repository.RepositoryFactory;
import com.google.common.collect.Lists;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

@Path("/customer")
public class CustomerResource {

    private UriInfo uriInfo;
    private CustomerRepository mongoCustomerRepository;
    private CreditLogRepository creditLogRepository;
    private OrderClient orderClient;


    public CustomerResource() {
        mongoCustomerRepository = RepositoryFactory.getCustomerRepository("localhost", 27017, "test");
        creditLogRepository = RepositoryFactory.getCreditLogRepository("localhost", 27017, "test");
        String orderServiceHost = System.getenv("ORDERS_HOST") != null ? System.getenv("ORDERS_HOST") : "localhost";
        int orderServicePort = System.getenv("ORDERS_PORT") != null ? Integer.valueOf(System.getenv("ORDERS_PORT")) : 8080;
        orderClient = new OrderClient(orderServiceHost, orderServicePort);
    }

    @POST
    @Consumes("application/json")
    public Response create(Customer customer) {

        final String customerId = mongoCustomerRepository.save(customer);

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("{id}").build(customerId))
                .build();
    }

    @GET
    @Timed
    @Path("{id}")
    @Produces("application/json")
    public Response getCustomer(@PathParam("id") String id) {

        Customer customer = mongoCustomerRepository.getCustomer(id);
        Link[] links = new Link[]{
                Link.fromUri(uriInfo.getBaseUriBuilder().path(CustomerResource.class).path(id).build())
                        .rel("self")
                        .build(),
                Link.fromUri(uriInfo.getBaseUriBuilder().path(CustomerResource.class).path(id).build())
                        .rel("update")
                        .type("PUT")
                        .build(),
                Link.fromUri(uriInfo.getBaseUriBuilder().path(CustomerResource.class).path(id).build())
                        .rel("delete")
                        .type("DELETE")
                        .build()
        };


        return Response.ok(customer)
                .links(links)
                .build();
    }

    @GET
    @Produces("application/json")
    public Response getAll() {

        List<Customer> customers = mongoCustomerRepository.getAll();

        List<Link> links = new ArrayList<>();

        for (Customer customer : customers) {
            links.add(
                    Link.fromUri(uriInfo.getBaseUriBuilder().path(CustomerResource.class)
                            .path(customer.getId()).build())
                            .rel("customer")
                            .build()
            );
        }

        return Response.ok(customers)
                .link(uriInfo.getBaseUriBuilder().path(CustomerResource.class).build(), "self")
                .links(links.toArray(new Link[links.size()]))
                .build();
    }

    @Path("{id}")
    @PUT
    @Consumes("application/json")
    public Response update(@PathParam("id") String customerId, Customer customer) {

        mongoCustomerRepository.update(customer);

        Link selfLink = Link.fromUri(uriInfo.getBaseUriBuilder().path("id").build())
                .rel("self").build();

        return Response.noContent()
                .links(selfLink)
                .build();
    }

    @Path("{id}")
    @DELETE
    public Response delete(@PathParam("id") String customerId) {

        mongoCustomerRepository.delete(customerId);
        return Response.noContent().build();
    }

    @Path("{id}/orders")
    @GET
    public Response getOrders(@PathParam("id") String customerId) {

        List<CreditLog> creditLogs = creditLogRepository.findByCustomer(customerId);

        List<Order> orders = new ArrayList<>();

        creditLogs.parallelStream()
                .forEach(creditLog -> orders.add(orderClient.getOrder(creditLog.getOrderId()).readEntity(Order.class)));

        List<Link> ordersLinks = new ArrayList<>();

        orders.stream()
                .forEach(order -> ordersLinks.add(
                        Link.fromUriBuilder(
                                UriBuilder.fromUri(orderClient.getOrderServiceUri()).path(order.getId())).rel("order")
                                .build()));

        return Response.ok(new GenericEntity<List<Order>>(orders) {
        })
                .link(uriInfo.getRequestUriBuilder().build(), "self")
                .links((Link[]) ordersLinks.toArray(new Link[ordersLinks.size()]))
                .build();
    }


    @Context
    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public void setMongoCustomerRepository(CustomerRepository mongoCustomerRepository) {
        this.mongoCustomerRepository = mongoCustomerRepository;
    }
}
