package com.finals.cinema.model.entity;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "projections")
public class Projection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(name = "starting_time")
    private LocalDateTime startAt;

    @Column(name = "end_time")
    private LocalDateTime endAt;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hall;
}


