package com.finals.cinema.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.finals.cinema.service.MovieService;
import com.finals.cinema.util.Constants;
import com.finals.cinema.util.exceptions.BadGetawayException;
import com.finals.cinema.util.exceptions.BadRequestException;
import com.finals.cinema.util.exceptions.UnauthorizedException;
import com.finals.cinema.model.DTO.*;
import com.finals.cinema.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.util.List;

@Component
@RestController
public class MovieController extends AbstractController {

    @Autowired
    private MovieService movieService;

    @GetMapping(value = "/movies/{movie_id}")
    public ResponseMovieDTO getMovieById(@PathVariable(name = "movie_id") int movieId) {
        return movieService.getMovieById(movieId);
    }

    @GetMapping(value = "/genre/{genre_id}/movies")
    public List<ResponseMovieDTO> getMovieByGenre(@PathVariable(name = "genre_id") int genreId) {
        return movieService.getMoviesByGenre(genreId);
    }

    @GetMapping(value = "/movies/title/{title}")
    public IMDBDataDTO findMoviesByName(@PathVariable(name = "title") String title) throws BadGetawayException, FileNotFoundException {
        JsonNode node = movieService.getImdbInfo(Constants.API_URL_SEARCH_ALL_FILMS + title);
        return new IMDBDataDTO(node);
    }

    @PostMapping(value = "/movies")
    public ResponseMovieDTO addMovie(@Valid @RequestBody addMovieDTO addMovieDTO, HttpSession ses) throws BadRequestException, UnauthorizedException, BadGetawayException, FileNotFoundException {
        User user = sessionManager.getLoggedUser(ses);
        return movieService.addMovie(addMovieDTO, user.getId());
    }

    @DeleteMapping(value = "/movies/{movie_id}")
    public ResponseMovieDTO deleteMovie(@PathVariable(name = "movie_id") int movieId, HttpSession ses) throws UnauthorizedException {
        User user = sessionManager.getLoggedUser(ses);
        return movieService.deleteMovie(movieId,user.getId());
    }
}

