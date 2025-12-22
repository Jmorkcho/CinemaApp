package com.finals.cinema.service;

import com.finals.cinema.model.entity.Genre;
import com.finals.cinema.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

  private final GenreRepository genreRepository;

  public List<Genre> getAllGenres() {
    log.info("Getting all genres");
    return genreRepository.findAll();
  }
}
