package com.finals.cinema.model.entity;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "projection_id")
    private Projection projection;

    @Column(name = "seat_number")
    private int seatNumber;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;
}