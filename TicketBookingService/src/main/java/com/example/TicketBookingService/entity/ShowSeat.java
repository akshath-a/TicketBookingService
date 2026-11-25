package com.example.TicketBookingService.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "show_seats",
        uniqueConstraints = @UniqueConstraint(name = "uk_show_seat", columnNames = {"show_id","seat_label"}))
public class ShowSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(name = "seat_label", nullable = false)
    private String seatLabel;

    @Column(nullable = false)
    private boolean reserved;

    @Version
    private Long version;

    public ShowSeat() {

    }

    public ShowSeat(Show show, String seatLabel) {
        this.show = show;
        this.seatLabel = seatLabel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeatLabel() {
        return seatLabel;
    }

    public boolean isReserved(){
        return reserved;
    }

    public void reserve() {
        if (this.reserved) {
            throw new IllegalStateException("Seat " + seatLabel + " is already reserved");
        }
        this.reserved = true;
    }

    public void release() {
        if (!this.reserved) {
            throw new IllegalStateException("Seat " + seatLabel + " is not currently reserved");
        }
        this.reserved = false;
    }

    public Show getShow() {
        return show;
    }

    public Long getVersion() {
        return version;
    }
}
