package com.finals.cinema.model.entity;

import com.finals.cinema.model.DTO.RequestHallDTO;
import lombok.*;

import jakarta.persistence.*;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "halls")
@NoArgsConstructor
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "number")
    private int number;

    @Column(name = "capacity")
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    @OneToMany(mappedBy = "hall", fetch = FetchType.LAZY)
    private List<Projection> projections;

    public Hall(RequestHallDTO requestHallDTO) {
        number = requestHallDTO.getNumber();
        capacity= requestHallDTO.getCapacity();
    }
}
