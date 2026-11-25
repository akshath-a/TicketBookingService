package com.example.TicketBookingService.dto;

public record AuthResponse(String token, String tokenType, long expiresInSeconds, ProfileResponse profile) {
}
