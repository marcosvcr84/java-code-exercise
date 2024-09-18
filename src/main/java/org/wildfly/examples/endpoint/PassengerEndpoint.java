package org.wildfly.examples.endpoint;

import org.wildfly.examples.entity.Passenger;
import org.wildfly.examples.service.PassengerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/passengers")
public class PassengerEndpoint {

    @Inject
    private PassengerService passengerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Passenger> listPassengers() {
        return passengerService.listPassengers();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPassenger(Passenger passenger) {
        passengerService.addPassenger(passenger);
        return Response.status(Response.Status.CREATED).build();
    }
}
