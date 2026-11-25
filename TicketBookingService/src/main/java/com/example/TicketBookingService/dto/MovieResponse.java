package com.example.TicketBookingService.dto;

import com.example.TicketBookingService.entity.Movie;

import java.io.Serializable;

public record MovieResponse(Long id, String title, String language, String genre, Integer durationMinutes,
                            String certificate, String description, String posterUrl, String trailerUrl) implements Serializable {
    public static MovieResponse from (Movie movie){
        return new MovieResponse(movie.getId(), movie.getTitle(), movie.getLanguage(), movie.getGenre(),
                movie.getDurationMinutes(), movie.getCertificate(), movie.getDescription(), movie.getPostedUrl(),
                movie.getTrailerUrl());
    }
}
