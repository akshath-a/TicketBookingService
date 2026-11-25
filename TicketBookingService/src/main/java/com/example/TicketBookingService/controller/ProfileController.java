package com.example.TicketBookingService.controller;

import com.example.TicketBookingService.config.JwtService;
import com.example.TicketBookingService.dto.*;
import com.example.TicketBookingService.service.BookingService;
import com.example.TicketBookingService.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final BookingService bookingService;
    private final JwtService jwtService;

    public ProfileController(ProfileService profileService, BookingService bookingService, JwtService jwtService) {
        this.profileService = profileService;
        this.bookingService = bookingService;
        this.jwtService = jwtService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse create(@Valid @RequestBody CreateProfileRequest request) {
        return profileService.create(request);
    }

    @PostMapping("/login")
    public AuthResponse issueToken(@Valid @RequestBody AuthRequest request) {
        ProfileResponse profile = profileService.login(request.identifier());
        String token = jwtService.generateToken(profile.id(), profile.email());
        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds(), profile);
    }

    @GetMapping("/login")
    public ProfileResponse login(@RequestParam String identifier) {
        return profileService.login(identifier);
    }

    @GetMapping("/{profileId}/bookings")
    public List<BookingResponse> bookings(@PathVariable long profileId) {
        return bookingService.findByProfileId(profileId);
    }
}