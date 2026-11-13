package com.finals.cinema.service;

import com.finals.cinema.model.entity.*;
import com.finals.cinema.repository.*;
import com.finals.cinema.util.exceptions.NotFoundException;
import com.finals.cinema.util.exceptions.UnauthorizedException;

import jakarta.transaction.Transactional;

import com.finals.cinema.model.DTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectionService {

    private final ProjectionRepository projectionRepository;
    private final HallRepository hallRepository;
    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;
    private final GenreRepository genreRepository;
    private final TicketRepository ticketRepository;

    public ResponseProjectionDTO getProjectionById(int id) {
        Optional<Projection> sProjection = projectionRepository.findById(id);
        if (sProjection.isEmpty()) {
            throw new NotFoundException("Projection does not exist");
        }
        return new ResponseProjectionDTO(sProjection.get());
    }

    @Transactional
    public ResponseProjectionDTO addProjection(RequestProjectionDTO requestProjectionDTO) {
        Optional<Hall> sHall = hallRepository.findById(requestProjectionDTO.getHallId());
        if (sHall.isEmpty()) {
            throw new NotFoundException("Hall not found");
        }
        Hall hall = sHall.get();
        Optional<Movie> sMovie = movieRepository.findById(requestProjectionDTO.getMovieId());
        if (sMovie.isEmpty()) {
            throw new NotFoundException("Movie with that Id does not exist");
        }
        Movie movie = sMovie.get();
        validateNoOverlap(hall, requestProjectionDTO.getStartAt(),
                requestProjectionDTO.getStartAt().plusMinutes(movie.getLength()), null);
        Projection projection = new Projection();
        projection.setMovie(movie);
        projection.setHall(hall);
        projection.setStartAt(requestProjectionDTO.getStartAt());
        projection.setEndAt(requestProjectionDTO.getStartAt().plusMinutes(movie.getLength()));
        return new ResponseProjectionDTO(projectionRepository.save(projection));
    }


    //TODO
//    public List<Integer> getFreePlaces(int projectionId) {
//        Optional<Projection> sProjection = projectionRepository.findById(projectionId);
//        if (sProjection.isEmpty()) {
//            throw new IllegalArgumentException("Projection does not exist");
//        }
//        return sProjection.get().getReservedSeats(projectionId);
//    }

    private void validateNoOverlap(Hall hall, LocalDateTime start, LocalDateTime end, Integer excludeProjectionId) {
        for (Projection p : projectionRepository.findByHall(hall)) {
            if (excludeProjectionId != null && excludeProjectionId.equals(p.getId())) {
                continue;
            }
            if (start.isBefore(p.getEndAt()) && end.isAfter(p.getStartAt())) {
                throw new IllegalArgumentException("There is already a projection during this time in the hall");
            }
        }
    }

    public List<Projection> getAllProjections() {
        return projectionRepository.findAll(org.springframework.data.domain.Sort.by("startAt"));
    }

    public void deleteProjection(int projectionId) {
        if (!projectionRepository.existsById(projectionId)) {
            throw new NotFoundException("No projection with that id");
        }
        if (ticketRepository.existsByProjection_Id(projectionId)) {
            throw new IllegalArgumentException("Cannot delete a projection that already has sold tickets");
        }
        projectionRepository.deleteById(projectionId);
    }
    public ResponseProjectionDTO removeProjection(int projectionId, int userId) throws UnauthorizedException {
        Optional<Projection> sProjection = projectionRepository.findById(projectionId);
        if (sProjection.isEmpty()) {
            throw new NotFoundException("No projection with that id");
        }
        ResponseProjectionDTO deletedProjection = new ResponseProjectionDTO(sProjection.get());
        projectionRepository.deleteById(projectionId);
        return deletedProjection;

    }

    public List<ResponseProjectionDTO> getProjectionByCinema(int cinemaId) {
        List<Projection> projections = new ArrayList<>();
        Optional<Cinema> sCinema = cinemaRepository.findById(cinemaId);
        if (sCinema.isEmpty()) {
            throw new NotFoundException("Cinema is not found");
        }
        for (Hall h : sCinema.get().getHalls()) {
            projections.addAll(projectionRepository.findByHallId(h.getId()));
        }
        if (projections.isEmpty()) {
            throw new NotFoundException("No projections found for this cinema");
        }
        List<ResponseProjectionDTO> projectionDTOS = new ArrayList<>();
        for (Projection p : projections) {
            projectionDTOS.add(new ResponseProjectionDTO(p));
        }
        return projectionDTOS;
    }

    public List<ResponseProjectionDTO> getProjectionByCity(String city) {
        List<Projection> projections = new ArrayList<>();
        List<Cinema> cinemas = cinemaRepository.findAllByCity(city);
        if (cinemas.isEmpty()) {
            throw new NotFoundException("No found cinemas in this city");
        }
        for (Cinema c : cinemas) {
            for (Hall h : c.getHalls()) {
                projections.addAll(projectionRepository.findByHallId(h.getId()));
            }
        }
        if (projections.isEmpty()) {
            throw new NotFoundException("No projections found for this city");
        }
        List<ResponseProjectionDTO> projectionDTOS = new ArrayList<>();
        for (Projection p : projections) {
            projectionDTOS.add(new ResponseProjectionDTO(p));
        }
        return projectionDTOS;
    }

    public ResponseProjectionDTO editProjection(RequestProjectionDTO requestProjectionDTO, int projectionId) {
        Optional<Hall> sHall = hallRepository.findById(requestProjectionDTO.getHallId());
        if (sHall.isEmpty()) {
            throw new NotFoundException("Hall not found");
        }
        Hall hall = sHall.get();
        Optional<Movie> sMovie = movieRepository.findById(requestProjectionDTO.getMovieId());
        if (sMovie.isEmpty()) {
            throw new NotFoundException("Movie with that id does not exist");
        }
        validateNoOverlap(hall, requestProjectionDTO.getStartAt(),
                requestProjectionDTO.getStartAt().plusMinutes(sMovie.get().getLength()), projectionId);
        Projection projection = projectionRepository.findById(projectionId)
                .orElseThrow(() -> new NotFoundException("No projection with that id"));
        projection.setHall(sHall.get());
        projection.setMovie(sMovie.get());
        projection.setStartAt(requestProjectionDTO.getStartAt());
        projection.setEndAt(requestProjectionDTO.getStartAt().plusMinutes(projection.getMovie().getLength()));
        return new ResponseProjectionDTO(projectionRepository.save(projection));
    }

    public List<ResponseProjectionDTO> getAllProjectionsByGenre(int genre_id) {
        List<Movie> sMovies = movieRepository.findAllByGenreId(genre_id);
        if (sMovies.isEmpty()) {
            throw new NotFoundException("There are no movies with that genre");
        }
        List<Projection> sProjections = new ArrayList<>();
        for (Movie m : sMovies) {
            sProjections.addAll(projectionRepository.findByMovie_id(m.getId()));
        }
        if (sProjections.isEmpty()) {
            throw new NotFoundException("There are no projections with that genre");
        }
        List<ResponseProjectionDTO> projectionDTOS = new ArrayList<>();
        for (Projection p : sProjections) {
            projectionDTOS.add(new ResponseProjectionDTO(p));
        }
        return projectionDTOS;
    }

    public List<GenreDTO> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        if (genres.isEmpty()) {
            throw new NotFoundException("No found genres");
        }
        List<GenreDTO> genreDTOS = new ArrayList<>();
        for (Genre g : genres) {
            genreDTOS.add(new GenreDTO(g));
        }
        return genreDTOS;
    }
}