package com.example.TicketBookingService.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "movies",
        uniqueConstraints = @UniqueConstraint(name = "uk_movie_title", columnNames = {"title", "language"}))
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String language;
    private String genre;
    private Integer durationMinutes;
    private String certificate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String postedUrl;

    @Column(length = 500)
    private String trailerUrl;
    private boolean active = true;

    public Movie() {

    }

    public Movie(String title, String language, String genre, Integer durationMinutes, String certificate, String description, String postedUrl, String trailerUrl) {
        this.title = title;
        this.language = language;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.certificate = certificate;
        this.description = description;
        this.postedUrl = postedUrl;
        this.trailerUrl = trailerUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostedUrl() {
        return postedUrl;
    }

    public void setPostedUrl(String postedUrl) {
        this.postedUrl = postedUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
