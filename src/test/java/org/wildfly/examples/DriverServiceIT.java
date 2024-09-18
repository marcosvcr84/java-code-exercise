package org.wildfly.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.examples.entity.Driver;
import org.wildfly.examples.service.DriverService;

import java.io.File;
import java.util.List;

@ExtendWith(ArquillianExtension.class)
public class DriverServiceIT {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "DriverServiceIT.war")
                .addPackage(DriverService.class.getPackage())
                .addPackage(Driver.class.getPackage())
                .addClass(Resources.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"), "beans.xml");
    }

    @Inject
    DriverService driverService;

    @Test
    public void testAddAndListDrivers() {
        Driver driver = new Driver();
        driver.setName("John Doe");
        driverService.addDriver(driver);

        List<Driver> drivers = driverService.listDrivers();
        assertEquals(1, drivers.size());
        assertEquals("John Doe", drivers.get(0).getName());
    }
}
