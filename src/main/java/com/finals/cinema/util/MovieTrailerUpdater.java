package com.finals.cinema.util;

import com.finals.cinema.model.entity.Movie;
import com.finals.cinema.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.json.JSONObject;

import java.util.List;

import static com.finals.cinema.util.Constants.API_KEY;
import static com.finals.cinema.util.Constants.API_HOST;

@Component
public class MovieTrailerUpdater implements CommandLineRunner {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public void run(String... args) {
        List<Movie> movies = movieRepository.findAll();
        RestTemplate restTemplate = new RestTemplate();

        for (Movie movie : movies) {
            if (movie.getImdbId() != null && (movie.getTrailer() == null || movie.getTrailer().isEmpty())) {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("X-RapidAPI-Key", API_KEY);
                    headers.set("X-RapidAPI-Host", API_HOST);
                    HttpEntity<String> entity = new HttpEntity<>(headers);

                    String url = "https://moviesminidatabase.p.rapidapi.com/movie/id/" + movie.getImdbId();
                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

                    JSONObject json = new JSONObject(response.getBody());
                    String trailerUrl = json.getJSONObject("results").optString("trailer", null);

                    if (trailerUrl != null && !trailerUrl.isEmpty()) {
                        movie.setTrailer(trailerUrl);
                        movieRepository.save(movie);
                        System.out.println("Updated trailer for: " + movie.getTitle());
                    }
                } catch (Exception e) {
                    System.out.println("Failed to update trailer for: " + movie.getTitle());
                }
            }
        }
    }
}