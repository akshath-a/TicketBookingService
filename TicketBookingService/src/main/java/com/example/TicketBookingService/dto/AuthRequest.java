package com.example.TicketBookingService.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank String identifier) {
}
