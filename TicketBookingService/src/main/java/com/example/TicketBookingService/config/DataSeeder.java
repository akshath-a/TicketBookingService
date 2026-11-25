package com.example.TicketBookingService.config;

import com.example.TicketBookingService.entity.Customer;
import com.example.TicketBookingService.entity.Movie;
import com.example.TicketBookingService.entity.Show;
import com.example.TicketBookingService.entity.ShowSeat;
import com.example.TicketBookingService.entity.Theatre;
import com.example.TicketBookingService.repository.CustomerRepository;
import com.example.TicketBookingService.repository.MovieRepository;
import com.example.TicketBookingService.repository.ShowRepository;
import com.example.TicketBookingService.repository.ShowSeatRepository;
import com.example.TicketBookingService.repository.TheatreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    @Transactional
    CommandLineRunner seedInitialData(
            CustomerRepository customerRepository,
            MovieRepository movieRepository,
            TheatreRepository theatreRepository,
            ShowRepository showRepository,
            ShowSeatRepository showSeatRepository
    ) {
        return args -> {
            if (movieRepository.count() > 0 || theatreRepository.count() > 0) {
                return;
            }

            // 1. Sample Customer
            Customer demoCustomer = customerRepository.findByEmail("demo@bookmyshow.local")
                    .orElseGet(() -> customerRepository.save(
                            new Customer("Demo User", "demo@bookmyshow.local", "9876543210")
                    ));

            // 2. Sample Movies
            Movie inception = movieRepository.save(new Movie(
                    "Inception",
                    "English",
                    "Sci-Fi",
                    148,
                    "UA",
                    "A thief who steals corporate secrets through dream-sharing technology.",
                    "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_QL75_UX380_CR0,0,380,562_.jpg",
                    "https://youtu.be/YoHD9XEInc0?si=2ETxwrmtshaaA6_1"
            ));

            Movie interstellar = movieRepository.save(new Movie(
                    "Interstellar",
                    "English",
                    "Adventure",
                    169,
                    "UA",
                    "A team of explorers travels through a wormhole in space.",
                    "https://media.themoviedb.org/t/p/w440_and_h660_face/nrSaXF39nDfAAeLKksRCyvSzI2a.jpg",
                    "https://youtu.be/zSWdZVtXT7E?si=h55X6SnKkzazdGRk"
            ));

            // 3. Sample Theatres
            Theatre phoenix = theatreRepository.save(new Theatre(
                    "PVR Phoenix",
                    "Mumbai",
                    "Lower Parel, Mumbai"
            ));

            Theatre rCity = theatreRepository.save(new Theatre(
                    "INOX R City",
                    "Mumbai",
                    "Ghatkopar West, Mumbai"
            ));

            // 4. Sample Shows (Today & Tomorrow so queries are always fresh)
//            LocalDate today = LocalDate.now();
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
            List<String> seatLabels = generateSeatLabels(2, 5); // A1..A5, B1..B5 (10 seats)

            createShowWithSeats(
                    showRepository,
                    showSeatRepository,
                    inception,
                    phoenix,
                    LocalDateTime.of(today, LocalTime.of(18, 30)),
                    LocalDateTime.of(today, LocalTime.of(21, 0)),
                    new BigDecimal("350.00"),
                    seatLabels
            );

            createShowWithSeats(
                    showRepository,
                    showSeatRepository,
                    interstellar,
                    rCity,
                    LocalDateTime.of(today, LocalTime.of(20, 0)),
                    LocalDateTime.of(today, LocalTime.of(22, 50)),
                    new BigDecimal("400.00"),
                    seatLabels
            );

            createShowWithSeats(
                    showRepository,
                    showSeatRepository,
                    inception,
                    phoenix,
                    LocalDateTime.of(today.plusDays(1), LocalTime.of(16, 0)),
                    LocalDateTime.of(today.plusDays(1), LocalTime.of(18, 30)),
                    new BigDecimal("300.00"),
                    seatLabels
            );

            // Keep demoCustomer referenced cleanly
            demoCustomer.getId();
        };
    }

    private void createShowWithSeats(
            ShowRepository showRepository,
            ShowSeatRepository showSeatRepository,
            Movie movie,
            Theatre theatre,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            BigDecimal price,
            List<String> seatLabels
    ) {
        if (showRepository.existsByMovieIdAndTheatreIdAndStartsAt(movie.getId(), theatre.getId(), startsAt)) {
            return;
        }
//        Show show = showRepository.save(new Show(movie, theatre, startsAt, endsAt, price, seatLabels.size()));
        Show show = new Show(movie, theatre, startsAt, endsAt, price, seatLabels.size());
        show.setAvailableSeats(seatLabels.size());
        show = showRepository.save(show);
        List<ShowSeat> seats = new ArrayList<>(seatLabels.size());
        for (String label : seatLabels) {
            seats.add(new ShowSeat(show, label));
        }
        showSeatRepository.saveAll(seats);
    }

    private List<String> generateSeatLabels(int rows, int cols) {
        List<String> labels = new ArrayList<>(rows * cols);
        for (int r = 0; r < rows; r++) {
            char rowChar = (char) ('A' + r);
            for (int c = 1; c <= cols; c++) {
                labels.add(rowChar + String.valueOf(c));
            }
        }
        return labels;
    }
}