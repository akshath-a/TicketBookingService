# TicketBookingService — Movie Ticket Booking Backend

A RESTful backend service built with **Java 21** and **Spring Boot 3** for browsing movies, theatres, and daily showtimes, managing customer profiles, and booking or cancelling movie seats with temporary seat holds, JWT authentication, PostgreSQL persistence, Redis locking/caching, and Docker support.

---

## Tech Stack

- **Language:** Java 21 (Records, Streams, `java.time`)
- **Framework:** Spring Boot 3 (Spring Web MVC, Spring Data JPA, Spring Security, Jakarta Bean Validation)
- **Database:** PostgreSQL (JPA / Hibernate)
- **Cache & Distributed Holds:** Redis (`SETNX` / atomic set-if-not-exists with TTL expiration)
- **Security:** Spring Security + JWT Authentication
- **Containerization:** Docker & Docker Compose

---
