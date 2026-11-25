package com.example.TicketBookingService.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Theatre theatre;

    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private BigDecimal ticketPrice;
    private int totalSeats;
    private int availableSeats;
    private boolean active = true;

    @Version
    private long version;

    public Show() {

    }

    public Show(Movie movie, Theatre theatre, LocalDateTime startsAt, LocalDateTime endsAt, BigDecimal ticketPrice, int totalSeats) {
        this.movie = movie;
        this.theatre = theatre;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.ticketPrice = ticketPrice;
        this.totalSeats = totalSeats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public void setTheatre(Theatre theatre) {
        this.theatre = theatre;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void reserve(int seats)
    {
        if(seats<=0 || seats > availableSeats)
        {
            throw new IllegalArgumentException("Not enough Seat Available");
        }

        availableSeats-=seats;
    }

    public void release(int seats) {
        if (seats <= 0) {
            throw new IllegalArgumentException("Seats to release must be positive");
        }
        if (availableSeats + seats > totalSeats) {
            throw new IllegalStateException("Cannot release more seats than total capacity");
        }
        availableSeats += seats;
    }

    @PrePersist
    @PreUpdate
    private void validateState() {
        if (availableSeats < 0 || availableSeats > totalSeats) {
            throw new IllegalStateException("Invalid seat count invariant");
        }
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}
