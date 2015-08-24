package org.apache.tamaya.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Created by Anatole on 23.08.2015.
 */
public interface ConfigProviderService {

    @GET
    @Path("/config/{id}/")
    @Produces("application/json")
    String getConfiguration(@PathParam("id") String configId);
}
