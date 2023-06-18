package com.finals.cinema.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "seats")
public class Seat {

    @Id
    private int id;
    @ManyToMany(mappedBy = "reservedSeats")
    @JsonBackReference
    List<Projection> projections;
}