package org.wildfly.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.examples.entity.Driver;
import org.wildfly.examples.entity.Passenger;
import org.wildfly.examples.entity.TaxiRide;
import org.wildfly.examples.service.DriverService;
import org.wildfly.examples.service.PassengerService;
import org.wildfly.examples.service.TaxiRideService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.io.File;

@ExtendWith(ArquillianExtension.class)
public class TaxiRideServiceIT {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "TaxiRideServiceIT.war")
                .addPackage(TaxiRideService.class.getPackage())
                .addPackage(TaxiRide.class.getPackage())
                .addPackage(DriverService.class.getPackage())
                .addPackage(PassengerService.class.getPackage())
                .addClass(Resources.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"), "beans.xml");
    }

    @Inject
    TaxiRideService taxiRideService;

    @Inject
    DriverService driverService;

    @Inject
    PassengerService passengerService;

    @BeforeEach
    public void cleanDatabase() {
        List<TaxiRide> rides = taxiRideService.listRides();
        for (TaxiRide ride : rides) {
            taxiRideService.deleteTaxiRide(ride.getId());
        }
    }

    @Test
    public void testAddAndListTaxiRides() {
        Driver driver = new Driver();
        driver.setName("John Doe");
        driverService.addDriver(driver);

        Passenger passenger = new Passenger();
        passenger.setName("Jane Doe");
        passenger.setAge(25);
        passengerService.addPassenger(passenger);

        TaxiRide ride = new TaxiRide();
        ride.setDriver(driver);
        ride.setPassengers(Collections.singletonList(passenger));
        ride.setCost(50.0);
        ride.setDuration(20);
        ride.setStartDate(new Date());
        ride.setEndDate(new Date());
        taxiRideService.addTaxiRide(ride);

        List<TaxiRide> rides = taxiRideService.listRides();
        assertEquals(1, rides.size());
        assertEquals(driver.getName(), rides.get(0).getDriver().getName());
        assertEquals(1, rides.get(0).getPassengers().size());
        assertEquals(passenger.getName(), rides.get(0).getPassengers().get(0).getName());
    }

    @Test
    public void testEditTaxiRide() {
        Driver driver = new Driver();
        driver.setName("John Doe");
        driverService.addDriver(driver);

        Passenger passenger = new Passenger();
        passenger.setName("Jane Doe");
        passenger.setAge(25);
        passengerService.addPassenger(passenger);

        TaxiRide ride = new TaxiRide();
        ride.setDriver(driver);
        ride.setPassengers(Collections.singletonList(passenger));
        ride.setCost(50.0);
        ride.setDuration(20);
        ride.setStartDate(new Date());
        ride.setEndDate(new Date());
        taxiRideService.addTaxiRide(ride);

        ride.setCost(60.0);
        taxiRideService.editTaxiRide(ride);

        List<TaxiRide> rides = taxiRideService.listRides();
        assertEquals(1, rides.size());
        assertEquals(60.0, rides.get(0).getCost());
    }

    @Test
    public void testDeleteTaxiRide() {
        Driver driver = new Driver();
        driver.setName("John Doe");
        driverService.addDriver(driver);

        Passenger passenger = new Passenger();
        passenger.setName("Jane Doe");
        passenger.setAge(25);
        passengerService.addPassenger(passenger);

        TaxiRide ride = new TaxiRide();
        ride.setDriver(driver);
        ride.setPassengers(Collections.singletonList(passenger));
        ride.setCost(50.0);
        ride.setDuration(20);
        ride.setStartDate(new Date());
        ride.setEndDate(new Date());
        taxiRideService.addTaxiRide(ride);

        List<TaxiRide> rides = taxiRideService.listRides();
        assertEquals(1, rides.size());

        taxiRideService.deleteTaxiRide(ride.getId());
        rides = taxiRideService.listRides();
        assertEquals(0, rides.size());
    }

    @Test
    public void testDeletePassengerFromRide() {
        Driver driver = new Driver();
        driver.setName("John Doe");
        driverService.addDriver(driver);

        Passenger passenger = new Passenger();
        passenger.setName("Jane Doe");
        passenger.setAge(25);
        passengerService.addPassenger(passenger);

        TaxiRide ride = new TaxiRide();
        ride.setDriver(driver);
        ride.setPassengers(Collections.singletonList(passenger));
        ride.setCost(50.0);
        ride.setDuration(20);
        ride.setStartDate(new Date());
        ride.setEndDate(new Date());
        taxiRideService.addTaxiRide(ride);

        List<TaxiRide> rides = taxiRideService.listRides();
        assertEquals(1, rides.size());
        assertEquals(1, rides.get(0).getPassengers().size());

        taxiRideService.deletePassengerFromRide(ride.getId(), passenger.getId());

        rides = taxiRideService.listRides();
        assertEquals(1, rides.size());
        assertEquals(0, rides.get(0).getPassengers().size());
    }

    @Test
    public void testReportEarningsByDriver() {
        Driver driver = new Driver();
        driver.setName("John Doe");
        driverService.addDriver(driver);

        Passenger passenger = new Passenger();
        passenger.setName("Jane Doe");
        passenger.setAge(25);
        passengerService.addPassenger(passenger);

        Date startDate = new Date();
        Date endDate = new Date();

        TaxiRide ride = new TaxiRide();
        ride.setDriver(driver);
        ride.setPassengers(Collections.singletonList(passenger));
        ride.setCost(50.0);
        ride.setDuration(20);
        ride.setStartDate(startDate);
        ride.setEndDate(endDate);
        taxiRideService.addTaxiRide(ride);

        List<Object[]> report = taxiRideService.reportEarningsByDriver(startDate, endDate, driver.getId());
        assertTrue(report.size() > 0);
        assertEquals(50.0, report.get(0)[1]);
    }
}
