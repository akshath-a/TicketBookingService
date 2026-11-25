package com.example.TicketBookingService.dto;

import com.example.TicketBookingService.entity.Theatre;

import java.io.Serializable;

public record TheatreResponse(Long id, String name, String city, String address) implements Serializable {
    public static TheatreResponse from(Theatre theatre){
        return new TheatreResponse(theatre.getId(), theatre.getName(), theatre.getCity(), theatre.getAddress());
    }
}
