package com.example.TicketBookingService.controller;

import com.example.TicketBookingService.dto.BookingResponse;
import com.example.TicketBookingService.dto.CreateBookingRequest;
import com.example.TicketBookingService.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService){
        this.bookingService = bookingService;
    }

    @PostMapping("/shows/{showId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse book(
            @PathVariable Long showId,
            @Valid @RequestBody CreateBookingRequest request,
            Authentication authentication
    ) {
        Long authProfileId = extractProfileId(authentication);
        return bookingService.book(showId, authProfileId, request);
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingResponse cancel(
            @PathVariable Long bookingId,
            @RequestBody(required = false) Long profileId,
            Authentication authentication
    ) {
        Long authProfileId = extractProfileId(authentication);
        Long effectiveProfileId = authProfileId != null ? authProfileId : profileId;
        return bookingService.cancel(bookingId, effectiveProfileId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse find(@PathVariable Long bookingId) {
        return bookingService.find(bookingId);
    }

    private Long extractProfileId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Long id) {
            return id;
        }
        return null;
    }
}
