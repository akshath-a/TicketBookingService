package com.example.TicketBookingService.service;

public class ProfileConflictException extends RuntimeException {
    public ProfileConflictException(String message) {
        super(message);
    }
}
