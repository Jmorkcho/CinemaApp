package com.finals.cinema.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "genres")
public class Genre {

    @Id
    private int id;
    private String type;


}

