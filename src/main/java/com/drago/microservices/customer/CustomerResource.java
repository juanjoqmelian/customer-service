package com.drago.microservices.customer;

import com.codahale.metrics.annotation.Timed;
import com.drago.microservices.customer.repository.CustomerRepository;
import com.drago.microservices.customer.repository.CustomerRepositoryFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

@Path("/customer")
public class CustomerResource {

    private UriInfo uriInfo;
    private CustomerRepository mongoCustomerRepository;


    public CustomerResource() {
        mongoCustomerRepository = CustomerRepositoryFactory.get("localhost", 27017, "test");
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
        Link selfLink = Link.fromUri(uriInfo.getBaseUriBuilder().path(CustomerResource.class).path(id).build())
                .rel("self")
                .build();
        return Response.ok(customer)
                .links(selfLink)
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


    @Context
    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public void setMongoCustomerRepository(CustomerRepository mongoCustomerRepository) {
        this.mongoCustomerRepository = mongoCustomerRepository;
    }
}
