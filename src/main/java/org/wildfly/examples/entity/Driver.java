package org.wildfly.examples.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "driver")
    @JsonIgnore
    private List<TaxiRide> rides;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public List<TaxiRide> getRides() {
        return rides;
    }

    public void setRides(List<TaxiRide> rides) {
        this.rides = rides;
    }
}
