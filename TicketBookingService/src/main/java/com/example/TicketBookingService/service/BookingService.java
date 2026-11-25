package com.example.TicketBookingService.service;

import com.example.TicketBookingService.dto.BookingResponse;
import com.example.TicketBookingService.dto.CreateBookingRequest;
import com.example.TicketBookingService.entity.Booking;
import com.example.TicketBookingService.entity.BookingStatus;
import com.example.TicketBookingService.entity.Show;
import com.example.TicketBookingService.entity.ShowSeat;
import com.example.TicketBookingService.repository.BookingRepository;
import com.example.TicketBookingService.repository.CustomerRepository;
import com.example.TicketBookingService.repository.ShowRepository;
import com.example.TicketBookingService.repository.ShowSeatRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class BookingService {

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final SeatHoldService seatHoldService;

    public BookingService(
            ShowRepository showRepository,
            ShowSeatRepository showSeatRepository,
            BookingRepository bookingRepository,
            CustomerRepository customerRepository,
            SeatHoldService seatHoldService
    ) {
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.seatHoldService = seatHoldService;
    }

    @Transactional
    @CacheEvict(value = "shows", allEntries = true)
    public BookingResponse book(Long showId, Long authenticatedProfileId, CreateBookingRequest request) {
        Long profileId = authenticatedProfileId != null ? authenticatedProfileId : request.profileId();
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));
        var customer = customerRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        List<String> labels = request.seatLabels().stream()
                .map(label -> label.trim().toUpperCase(Locale.ROOT))
                .toList();

        if (labels.stream().distinct().count() != labels.size()) {
            throw new SeatUnavailableException("Duplicate seat labels are not allowed");
        }

        boolean holdAcquired = seatHoldService.acquireHolds(showId, labels, profileId);
        if (!holdAcquired) {
            throw new SeatUnavailableException("One or more selected seats are currently held or unavailable");
        }

        try {
            List<ShowSeat> seats = showSeatRepository.findForUpdate(showId, labels);
            if (seats.size() != labels.size() || seats.stream().anyMatch(ShowSeat::isReserved)) {
                throw new SeatUnavailableException("One or more selected seats are unavailable");
            }

            seats.forEach(ShowSeat::reserve);
            show.reserve(labels.size());

            BigDecimal total = show.getTicketPrice().multiply(BigDecimal.valueOf(labels.size()));
            Booking booking = bookingRepository.save(new Booking(show, customer, total, labels));
            return BookingResponse.from(booking);
        } finally {
            seatHoldService.releaseHolds(showId, labels, profileId);
        }
    }

    @Transactional(readOnly = true)
    public BookingResponse find(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findByProfileId(Long profileId) {
        if (!customerRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile not found");
        }
        return bookingRepository.findByCustomerIdOrderByBookedAtDesc(profileId).stream()
                .map(BookingResponse::from).toList();
    }

    @Transactional
    @CacheEvict(value = "shows", allEntries = true)
    public BookingResponse cancel(Long bookingId, Long profileId) {
        Booking booking = bookingRepository.findByIdAndCustomerId(bookingId, profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            List<ShowSeat> seats = showSeatRepository.findForUpdate(booking.getShow().getId(), booking.getSeatLabels());
            seats.forEach(ShowSeat::release);
            booking.getShow().release(booking.getSeatLabels().size());
            booking.cancel();
        }
        return BookingResponse.from(booking);
    }
}