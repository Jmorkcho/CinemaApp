package com.finals.cinema.model.entity;

import lombok.*;

import jakarta.persistence.*;

import java.math.BigDecimal;


@Getter
@Setter
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "year")
    private String year;

    @Column(name = "plot")
    private String plot;

    @Column(name = "length")
    private int length;

    @Column(name = "rating")
    private BigDecimal rating;

    @Column(name = "age_restriction")
    private int ageRestriction;

    @Column(name = "leading_actor")
    private String leadingActor;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Column(name = "poster")
    private String poster;

    @Column(name = "imdb_id")
    private String imdbId;
}