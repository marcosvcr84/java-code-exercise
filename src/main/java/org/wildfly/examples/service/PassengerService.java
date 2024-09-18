package org.wildfly.examples.service;

import org.wildfly.examples.entity.Passenger;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

@Stateless
public class PassengerService {
    @Inject
    private EntityManager em;

    public List<Passenger> listPassengers() {
        return em.createQuery("SELECT p FROM Passenger p", Passenger.class).getResultList();
    }

    public void addPassenger(Passenger passenger) {
        em.persist(passenger);
    }
}
