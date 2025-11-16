package com.finals.cinema.model.DTO;

import com.finals.cinema.model.entity.Genre;
import com.finals.cinema.model.entity.Movie;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseMovieDTO {

    private int id;
    private String title;
    private String year;
    private String plot;
    private int length;
    private double rating;
    private int ageRestriction;
    private String leadingActor;
    private Genre genre;
    private String poster;
    private String imdb_id;

    public ResponseMovieDTO(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.year = movie.getYear();
        this.plot = movie.getPlot();
        this.length = movie.getLength();
        this.rating = movie.getRating();
        this.ageRestriction = movie.getAgeRestriction();
        this.leadingActor = movie.getLeadingActor();
        this.genre = movie.getGenre();
        this.poster = movie.getPoster();
        this.imdb_id = movie.getImdbId();
    }
}
