package org.wildfly.examples.service;

import org.wildfly.examples.entity.Driver;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

@Stateless
public class DriverService {
    @Inject
    private EntityManager em;

    public List<Driver> listDrivers() {
        return em.createQuery("SELECT d FROM Driver d", Driver.class).getResultList();
    }

    public void addDriver(Driver driver) {
        em.persist(driver);
    }
}
