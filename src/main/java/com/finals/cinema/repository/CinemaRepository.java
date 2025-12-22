package com.finals.cinema.repository;

import com.finals.cinema.model.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Integer> {

    List<Cinema> findAllByCity(String city);
    boolean existsByCityAndName(String city, String name);



}
