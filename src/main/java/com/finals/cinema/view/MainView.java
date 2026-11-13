package com.finals.cinema.view;

import com.finals.cinema.model.entity.Movie;
import com.finals.cinema.repository.MovieRepository;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;

import java.util.List;

import static com.finals.cinema.util.Constants.MAIN_VIEW_ROUTE;

@Route(value = MAIN_VIEW_ROUTE, layout = MainLayout.class)
@PageTitle("Main")
// TODO
public class MainView extends VerticalLayout {

    Grid<Movie> cinemaGrid = new Grid<>(Movie.class, false);

    public MainView(MovieRepository movieRepository) {
        System.out.println("MainView loaded");
        addClassName("status-list-view");
        setSizeFull();
        configureGrid(movieRepository);
        // add(getContent(movieRepository));
        add(createNowShowingSection(movieRepository));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        RouteConfiguration.forSessionScope().getAvailableRoutes()
            .forEach(route -> System.out.println("Registered route: " + route.getTemplate()));
    }

    private Component getContent(MovieRepository movieRepository) {
        HorizontalLayout content = new HorizontalLayout();
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }

    private Component createNowShowingSection(MovieRepository movieRepository) {
        Div movieGrid = new Div();
        movieGrid.addClassName("movie-grid");

        for (Movie movie : movieRepository.findAll()) {
            Div card = new Div();
            card.addClassName("movie-card");

            Div posterBox = new Div();
            posterBox.addClassName("movie-card-poster");
            String poster = movie.getPoster();
            if (poster != null && !poster.isBlank()) {
                Image image = new Image(poster, movie.getTitle());
                image.getElement().setAttribute("loading", "lazy");
                posterBox.add(image);
            } else {
                posterBox.add(new Span("No poster"));
            }

            Span title = new Span(movie.getTitle());
            title.addClassName("movie-card-title");
            card.add(posterBox, title);

            card.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("movie-details/" + movie.getId())));
            movieGrid.add(card);
        }
        return movieGrid;
    }
    private void configureGrid(MovieRepository movieRepository) {
        List<Movie> movies = movieRepository.findAll();
        cinemaGrid.addColumn(i -> i).setHeader("Num");
        Image image = new Image("/images/spider.jpg", "alt message");
        cinemaGrid.addComponentColumn(i -> image).setHeader("images");
        cinemaGrid.setItems(movies);

        cinemaGrid.setSizeFull();

        // cinemaGrid.addColumn(createMovieRenderer()).setAutoWidth(true).setFlexGrow(0);
        // cinemaGrid.addColumn(createMovieRenderer()).setAutoWidth(true).setFlexGrow(0);
    }

    // private ComponentRenderer<Span, Movie> createMovieRenderer() {
    // return new ComponentRenderer<>(Span::new, generatePopularMovies);
    // }

    // private final SerializableBiConsumer<Span, Movie> generatePopularMovies =
    // (span, movie) ->{
    // Optional<Movie> movieDB = movieRepository.findById(movie.getId());
    // movieDB.ifPresent( m ->
    // span.getElement().setAttribute("picture", m.getPoster()));
    //
    // };
}
