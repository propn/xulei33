package com.propn.golf.mvc;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("/version")
@Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML })
public class Version {

    @GET
    @Path("/get")
    public String getVersion(@Context
    HttpHeaders headers) {
        String version = "1.0";
        return version;
    }

    @GET
    @Path("/getVersion/{pathv}/{abc}")
    public String getVersion2(@Context
    HttpHeaders headers) {
        String version = "";
        return version;
    }
}
