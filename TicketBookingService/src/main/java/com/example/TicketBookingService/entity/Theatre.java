package com.example.TicketBookingService.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "theatres",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_theatre_name_city_address",
                    columnNames = {"name", "city", "address"}
            )
        },
        indexes = @Index(name = "idx_theatre_city", columnList = "city"))
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, length = 500)
    private String address;

    public Theatre() {

    }

    public Theatre(String name, String city, String address) {
        this.name = name;
        this.city = city;
        this.address = address;
    }

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
