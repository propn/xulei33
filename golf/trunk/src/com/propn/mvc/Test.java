package com.propn.mvc;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

@Path("/Test")
public class Test {

    @GET
    @Produces("text/plain")
    public String getNumber(@Context
    HttpHeaders headers) {
        return "1 Secured with User";
    }

    @GET
    @Path("/Secure")
    @Produces("text/plain")
    public String getSecureNumber(@Context
    HttpHeaders headers) {
        return "2 Secured with Admin";
    }

}
