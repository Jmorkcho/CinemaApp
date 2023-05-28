package com.finals.cinema.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finals.cinema.model.DTO.IMDBMovieDTO;
import com.finals.cinema.util.exceptions.BadGetawayException;
import com.finals.cinema.util.exceptions.BadRequestException;
import com.finals.cinema.util.exceptions.NotFoundException;
import com.finals.cinema.util.exceptions.UnauthorizedException;
import com.finals.cinema.model.DTO.AddMovieDTO;
import com.finals.cinema.model.DTO.ResponseMovieDTO;
import com.finals.cinema.model.entity.Genre;
import com.finals.cinema.model.entity.Movie;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.finals.cinema.util.Constants.*;

@Service
public class MovieService extends com.finals.cinema.service.AbstractService {

    public ResponseMovieDTO getMovieById(int movieId) {
        Optional<Movie> sMovie = movieRepository.findById(movieId);
        if (sMovie.isEmpty()) {
            throw new NotFoundException("Movie does not exist");
        }
        return new ResponseMovieDTO(sMovie.get());
    }

    public List<ResponseMovieDTO> getMoviesByGenre(int genreId) {
        List<Movie> sMovies = movieRepository.findAllByGenreId(genreId);
        if (sMovies.isEmpty()) {
            throw new NotFoundException("There are no movies with that genre");
        }
        List<ResponseMovieDTO> movies = new ArrayList<>();
        for (Movie m : sMovies) {
            movies.add(new ResponseMovieDTO(m));
        }
        return movies;
    }

    public ResponseMovieDTO addMovie(AddMovieDTO addMovieDTO, int userId) throws Exception, UnauthorizedException {
//        if (!isAdmin(userId)) {
//            throw new UnauthorizedException("Only admins can add movies");
//        }
        Movie sMovie = movieRepository.findByTitle(addMovieDTO.getTitle());
        if (sMovie != null) {
            throw new BadRequestException("There is already a movie with that title");
        }
        Optional<Genre> sGenre = genreRepository.findById(addMovieDTO.getGenreId());
        if (sGenre.isEmpty()) {
            throw new BadRequestException("Invalid genre");
        }
        IMDBMovieDTO imdb = addMovie(addMovieDTO);
        if (imdb.getImdbId().isBlank() || imdb.getImdbId() == null) {
            throw new BadRequestException("Movie with that title does not exist");
        }
        Movie movie = Movie.builder()
                .title(imdb.getTitle())
                .year(imdb.getYear())
                .plot(imdb.getPlot())
                .length(imdb.getLength())
                .rating(imdb.getRating())
                .ageRestriction(addMovieDTO.getAgeRestriction())
                .leadingActor(imdb.getLead())
                .genre(sGenre.get())
                .poster(imdb.getPoster())
                .imdbId(imdb.getImdbId())
                .projections(new ArrayList<>())
                .build();
        return new ResponseMovieDTO(movieRepository.save(movie));
    }

    private IMDBMovieDTO addMovie(AddMovieDTO addMovieDTO) throws Exception {
        String title = addMovieDTO.getTitle().replaceAll("\\s", "");
        try {
            String imdbId = getImdbId(title);
            JsonNode node = getImdbInfo(imdbId);
            IMDBMovieDTO imdb = new IMDBMovieDTO(node);
            return imdb;
        }catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    private String getImdbId(String title) throws BadGetawayException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL_SEARCH_BY_NAME + title))
                .header("X-RapidAPI-Key", API_KEY)
                .header("X-RapidAPI-Host", API_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        JsonNode jsonNode;
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper om = new ObjectMapper();
            jsonNode = om.readTree(response.body());
        } catch (Exception e) {
            throw new BadGetawayException("Cannot connect to IMDB,please try again later");
        }
        System.out.println(jsonNode.asText());
        System.out.println(jsonNode);
        return jsonNode.get("imdb_id").asText().trim();
    }

    public ResponseMovieDTO deleteMovie(int movieId, int userId) throws UnauthorizedException {
        if (!isAdmin(userId)) {
            throw new UnauthorizedException("Only admins can remove movies");
        }
        Optional<Movie> sMovie = movieRepository.findById(movieId);
        if (sMovie.isEmpty()) {
            throw new NotFoundException("Movie does not exist");
        }
        movieRepository.deleteById(movieId);
        return new ResponseMovieDTO(sMovie.get());
    }

    public JsonNode getImdbInfo(String id) throws BadGetawayException, FileNotFoundException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL_SEARCH_BY_IMDB_ID + id))
                .header("X-RapidAPI-Key", API_KEY)
                .header("X-RapidAPI-Host", API_HOST)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        JsonNode jsonNode;
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper om = new ObjectMapper();
            jsonNode = om.readTree(response.body());
        } catch (Exception e) {
            throw new BadGetawayException("Cannot connect to IMDB,please try again later");
        }
        return jsonNode;
    }
}
