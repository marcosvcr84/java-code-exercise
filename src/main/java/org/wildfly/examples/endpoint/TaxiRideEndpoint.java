package org.wildfly.examples.endpoint;

import org.wildfly.examples.entity.TaxiRide;
import org.wildfly.examples.service.TaxiRideService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Path("/taxirides")
public class TaxiRideEndpoint {

    @Inject
    private TaxiRideService rideService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TaxiRide> listRides(@QueryParam("startDate") Date startDate,
                                    @QueryParam("endDate") Date endDate,
                                    @QueryParam("minCost") Double minCost,
                                    @QueryParam("maxCost") Double maxCost,
                                    @QueryParam("minDuration") Long minDuration,
                                    @QueryParam("maxDuration") Long maxDuration,
                                    @QueryParam("driverId") Long driverId,
                                    @QueryParam("passengerId") Long passengerId,
                                    @QueryParam("passengerAge") Integer passengerAge) {
        return rideService.filterRides(startDate, endDate, minCost, maxCost, minDuration, maxDuration, driverId, passengerId, passengerAge);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTaxiRide(TaxiRide ride) {
        rideService.addTaxiRide(ride);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editTaxiRide(@PathParam("id") Long id, TaxiRide ride) {
        ride.setId(id);
        rideService.editTaxiRide(ride);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTaxiRide(@PathParam("id") Long id) {
        rideService.deleteTaxiRide(id);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{rideId}/passengers/{passengerId}")
    public Response deletePassengerFromRide(@PathParam("rideId") Long rideId, @PathParam("passengerId") Long passengerId) {
        rideService.deletePassengerFromRide(rideId, passengerId);
        return Response.noContent().build();
    }

    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Object[]> reportEarningsByDriver(@QueryParam("startDate") String startDateStr,
                                                 @QueryParam("endDate") String endDateStr,
                                                 @QueryParam("driverId") Long driverId) {
        Date startDate = parseDate(startDateStr);
        Date endDate = parseDate(endDateStr);
        return rideService.reportEarningsByDriver(startDate, endDate, driverId);
    }

    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            throw new BadRequestException("Invalid date format, expected 'yyyy-MM-dd'.");
        }
    }

}
