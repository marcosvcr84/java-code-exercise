package org.wildfly.examples.service;

import org.wildfly.examples.entity.TaxiRide;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class TaxiRideService {
    @Inject
    private EntityManager em;

    public List<TaxiRide> listRides() {
        return em.createQuery("SELECT r FROM TaxiRide r", TaxiRide.class).getResultList();
    }

    public void addTaxiRide(TaxiRide ride) {
        em.persist(ride);
    }

    public void editTaxiRide(TaxiRide ride) {
        em.merge(ride);
    }

    public void deleteTaxiRide(Long id) {
        TaxiRide ride = em.find(TaxiRide.class, id);
        if (ride != null) {
            em.remove(ride);
        }
    }

    public void deletePassengerFromRide(Long rideId, Long passengerId) {
        TaxiRide ride = em.find(TaxiRide.class, rideId);
        if (ride != null) {
            ride.getPassengers().removeIf(p -> p.getId().equals(passengerId));
            em.merge(ride);
        }
    }

    public List<TaxiRide> filterRides(Date startDate, Date endDate, Double minCost, Double maxCost, Long minDuration, Long maxDuration, Long driverId, Long passengerId, Integer passengerAge) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TaxiRide> query = cb.createQuery(TaxiRide.class);
        Root<TaxiRide> ride = query.from(TaxiRide.class);

        Predicate[] predicates = new Predicate[0];

        if (startDate != null) {
            predicates = append(predicates, cb.greaterThanOrEqualTo(ride.get("startDate"), startDate));
        }
        if (endDate != null) {
            predicates = append(predicates, cb.lessThanOrEqualTo(ride.get("endDate"), endDate));
        }
        if (minCost != null) {
            predicates = append(predicates, cb.greaterThanOrEqualTo(ride.get("cost"), minCost));
        }
        if (maxCost != null) {
            predicates = append(predicates, cb.lessThanOrEqualTo(ride.get("cost"), maxCost));
        }
        if (minDuration != null) {
            predicates = append(predicates, cb.greaterThanOrEqualTo(ride.get("duration"), minDuration));
        }
        if (maxDuration != null) {
            predicates = append(predicates, cb.lessThanOrEqualTo(ride.get("duration"), maxDuration));
        }
        if (driverId != null) {
            predicates = append(predicates, cb.equal(ride.get("driver").get("id"), driverId));
        }
        if (passengerId != null) {
            predicates = append(predicates, cb.isMember(passengerId, ride.get("passengers")));
        }
        if (passengerAge != null) {
            predicates = append(predicates, cb.lessThan(ride.join("passengers").get("age"), passengerAge));
        }

        query.where(cb.and(predicates));
        return em.createQuery(query).getResultList();
    }

    public List<Object[]> reportEarningsByDriver(Date startDate, Date endDate, Long driverId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<TaxiRide> ride = query.from(TaxiRide.class);

        query.multiselect(
                ride.get("driver").get("id"),
                cb.sum(ride.get("cost")),
                cb.avg(ride.get("duration")),
                cb.count(ride),
                cb.sum(
                        cb.<Long>selectCase()
                                .when(cb.lessThan(ride.join("passengers").get("age"), 18), cb.literal(1L))
                                .otherwise(cb.literal(0L))
                )
        );

        List<Predicate> predicates = new ArrayList<>();
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(ride.get("startDate"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(ride.get("endDate"), endDate));
        }
        if (driverId != null) {
            predicates.add(cb.equal(ride.get("driver").get("id"), driverId));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.groupBy(ride.get("driver").get("id"));
        return em.createQuery(query).getResultList();
    }

    private Predicate[] append(Predicate[] array, Predicate element) {
        Predicate[] newArray = new Predicate[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = element;
        return newArray;
    }
}
