package org.wildfly.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.examples.entity.Passenger;
import org.wildfly.examples.service.PassengerService;

import java.io.File;
import java.util.List;

@ExtendWith(ArquillianExtension.class)
public class PassengerServiceIT {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "PassengerServiceIT.war")
                .addPackage(PassengerService.class.getPackage())
                .addPackage(Passenger.class.getPackage())
                .addClass(Resources.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"), "beans.xml");
    }

    @Inject
    PassengerService passengerService;

    @Test
    public void testAddAndListPassengers() {
        Passenger passenger = new Passenger();
        passenger.setName("Jane Doe");
        passenger.setAge(30);
        passengerService.addPassenger(passenger);

        List<Passenger> passengers = passengerService.listPassengers();
        assertEquals(1, passengers.size());
        assertEquals("Jane Doe", passengers.get(0).getName());
    }
}
