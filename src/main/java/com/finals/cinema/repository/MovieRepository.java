package com.finals.cinema.repository;

import com.finals.cinema.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    List<Movie> findAllByGenreId(int genreId);
    Movie findByTitle(String title);
}
