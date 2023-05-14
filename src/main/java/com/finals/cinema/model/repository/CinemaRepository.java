package com.finals.cinema.model.repository;

import com.finals.cinema.model.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CinemaRepository extends JpaRepository<Cinema, Integer> {

    List<Cinema> findAllByCity(String city);
    Cinema findByCityAndName(String city,String name);



}
