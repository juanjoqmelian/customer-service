package com.drago.microservices.customer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Path("/")
public class RootResource {

    private UriInfo uriInfo;

    @GET
    public Response root() {

        return Response.ok()
                .link(UriBuilder.fromUri(uriInfo.getBaseUri()).path("customer").build(), "customer")
                        .build();
    }


    @Context
    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }
}
