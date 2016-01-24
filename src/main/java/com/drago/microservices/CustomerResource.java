package com.drago.microservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.UUID;

@Path("/customer")
public class CustomerResource {

    private UriInfo uriInfo;


    @POST
    @Consumes("application/json")
    public Response create(Customer customer) {

        String randomId = UUID.randomUUID().toString();
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("{id}").build(randomId))
                .build();
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getCustomer(@PathParam("id") String id) {

        Customer customer = new Customer(id, "Jon", 50000);
        return Response.ok(customer).build();
    }

    @GET
    @Produces("application/json")
    public Response getAll() {

        return Response.ok(Arrays.asList(
                new Customer(UUID.randomUUID().toString(), "Jon", 213231),
                new Customer(UUID.randomUUID().toString(), "Mary", 566576),
                new Customer(UUID.randomUUID().toString(), "Anne", 54353435))).build();
    }


    @Context
    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }
}
