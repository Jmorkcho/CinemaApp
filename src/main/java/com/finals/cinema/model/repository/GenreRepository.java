package com.finals.cinema.model.repository;

import com.finals.cinema.model.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Integer> {

    List<Genre> findAllByType(String s);
}

