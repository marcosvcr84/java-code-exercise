package org.wildfly.examples.endpoint;

import org.wildfly.examples.entity.Driver;
import org.wildfly.examples.service.DriverService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/drivers")
public class DriverEndpoint {

    @Inject
    private DriverService driverService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Driver> listDrivers() {
        return driverService.listDrivers();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addDriver(Driver driver) {
        driverService.addDriver(driver);
        return Response.status(Response.Status.CREATED).build();
    }
}
