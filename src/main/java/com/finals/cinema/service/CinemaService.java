package com.finals.cinema.service;

import com.finals.cinema.model.DTO.RequestCinemaDTO;
import com.finals.cinema.repository.CinemaRepository;
import com.finals.cinema.util.exceptions.*;
import com.finals.cinema.model.DTO.ResponseCinemaDTO;
import com.finals.cinema.model.entity.Cinema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CinemaService {

    private final CinemaRepository cinemaRepository;

    public List<ResponseCinemaDTO> getAllCinemas() {
       log.info("Getting all cinemas");
       return cinemaRepository.findAll()
         .stream()
         .filter(Objects::nonNull)
         .map(ResponseCinemaDTO::new)
         .toList();
    }

    public ResponseCinemaDTO getCinemaById(int cinemaId) {
        log.info("Getting cinema with id {}", cinemaId);
        Cinema cinema = cinemaRepository.findById(cinemaId)
          .orElseThrow(() -> new NotFoundException("Cinema not found"));
        return new ResponseCinemaDTO(cinema);
    }

    public List<ResponseCinemaDTO> getAllCinemasByCity(String city) throws NotFoundException {
        log.info("Getting all cinemas by city {}", city);
        return cinemaRepository.findAllByCity(city).stream()
          .filter(Objects::nonNull)
          .map(ResponseCinemaDTO::new)
          .toList();
    }

    public ResponseCinemaDTO addCinema(RequestCinemaDTO requestCinemaDTO) {
        log.info("Creating cinema");
        if (cinemaRepository.existsByCityAndName(requestCinemaDTO.getCity(), requestCinemaDTO.getName())){
            throw new IllegalArgumentException("There is already a cinema with that name in that city");
        }
        Cinema cinema = new Cinema();
        cinema.setName(requestCinemaDTO.getName());
        cinema.setCity(requestCinemaDTO.getCity());
      return new ResponseCinemaDTO(cinemaRepository.save(cinema));
    }

    public ResponseCinemaDTO removeCinema(int cinemaId) {
        log.info("Removing cinema with id {}", cinemaId);
        Cinema cinema = cinemaRepository.findById(cinemaId).orElseThrow(() -> new NotFoundException("Cinema does not exist"));
        ResponseCinemaDTO cinemaForDelete = new ResponseCinemaDTO(cinema);
        cinemaRepository.delete(cinema);
        return cinemaForDelete;
    }

    public ResponseCinemaDTO editCinema(RequestCinemaDTO requestCinemaDTO, int cinemaId) {
        Cinema cinema = cinemaRepository.findById(cinemaId).orElseThrow(() -> new NotFoundException("Cinema does not exist"));
        if (cinema.getName().equals(requestCinemaDTO.getName()) && cinema.getCity().equals(requestCinemaDTO.getCity())) {
            throw new IllegalArgumentException("You need to change the fields for an edit");
        }
        cinema.setCity(requestCinemaDTO.getCity());
        cinema.setName(requestCinemaDTO.getName());
        return new ResponseCinemaDTO(cinemaRepository.save(cinema));
    }
}