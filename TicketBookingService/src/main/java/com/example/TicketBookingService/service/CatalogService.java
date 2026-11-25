package com.example.TicketBookingService.service;

import com.example.TicketBookingService.dto.MovieResponse;
import com.example.TicketBookingService.dto.ShowResponse;
import com.example.TicketBookingService.dto.TheatreResponse;
import com.example.TicketBookingService.repository.MovieRepository;
import com.example.TicketBookingService.repository.ShowRepository;
import com.example.TicketBookingService.repository.ShowSeatRepository;
import com.example.TicketBookingService.repository.TheatreRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CatalogService {
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;

    public CatalogService(MovieRepository movieRepository, TheatreRepository theatreRepository, ShowRepository showRepository, ShowSeatRepository showSeatRepository) {
        this.movieRepository = movieRepository;
        this.theatreRepository = theatreRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
    }

    @Cacheable("movies")
    public List<MovieResponse> movies()
    {
        return movieRepository.findByActiveTrueOrderByTitle().stream().map(MovieResponse::from).toList();
    }

    @Cacheable(value = "theatres", key = "#city.trim().toLowerCase()")
    public List<TheatreResponse> theatres(String city)
    {
        return theatreRepository.findByCityIgnoreCaseOrderByName(city).stream().map(TheatreResponse::from).toList();
    }

    @Cacheable(value = "shows", key = "#city.trim().toLowerCase() + ':' + #date")
    public List<ShowResponse> shows(String city, LocalDate date)
    {
        LocalDateTime from=date.atStartOfDay();
        return showRepository.findActiveShows(city,from,from.plusDays(1)).stream()
                .map(show -> ShowResponse.from(show,showSeatRepository.findAvailableLabels(show.getId()))).toList();
    }
}
