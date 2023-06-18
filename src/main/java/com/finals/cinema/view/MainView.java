package com.finals.cinema.view;

import com.finals.cinema.model.entity.Movie;
import com.finals.cinema.model.repository.MovieRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.stream.IntStream;

import static com.finals.cinema.util.Constants.MAIN_VIEW_ROUTE;

@Route(value = MAIN_VIEW_ROUTE, layout = MainLayout.class)
@PageTitle("Main")
//TODO
@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
public class MainView extends VerticalLayout {

    Grid<Movie> cinemaGrid = new Grid<>(Movie.class, false);


    public MainView(MovieRepository movieRepository) {
        addClassName("status-list-view");
        setSizeFull();
//        configureGrid(movieRepository);
        add(getContent(movieRepository));

    }


    private Component getContent(MovieRepository movieRepository) {
        HorizontalLayout content = new HorizontalLayout();
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private void configureGrid(MovieRepository movieRepository) {
        List<Movie> movies = movieRepository.findAll();
        cinemaGrid.addColumn(i -> i).setHeader("Num");
        Image image = new Image("/images/spider.jpg", "alt message");
        cinemaGrid.addComponentColumn(i -> image).setHeader("images");
        cinemaGrid.setItems(movies);

        cinemaGrid.setSizeFull();

//        cinemaGrid.addColumn(createMovieRenderer()).setAutoWidth(true).setFlexGrow(0);
//        cinemaGrid.addColumn(createMovieRenderer()).setAutoWidth(true).setFlexGrow(0);
    }

//    private ComponentRenderer<Span, Movie> createMovieRenderer() {
//    return new ComponentRenderer<>(Span::new, generatePopularMovies);
//    }

//    private  final SerializableBiConsumer<Span, Movie> generatePopularMovies = (span, movie) ->{
//        Optional<Movie> movieDB = movieRepository.findById(movie.getId());
//        movieDB.ifPresent( m ->
//                span.getElement().setAttribute("picture", m.getPoster()));
//
//    };
}



