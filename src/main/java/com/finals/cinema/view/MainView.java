package com.finals.cinema.view;

import com.finals.cinema.model.entity.Movie;
import com.finals.cinema.model.repository.MovieRepository;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;

import java.util.List;
import java.util.stream.IntStream;

import static com.finals.cinema.util.Constants.MAIN_VIEW_ROUTE;

@Route(value = MAIN_VIEW_ROUTE, layout = MainLayout.class)
@PageTitle("Main")
// TODO
public class MainView extends VerticalLayout {

    Grid<Movie> cinemaGrid = new Grid<>(Movie.class, false);

    public MainView(MovieRepository movieRepository) {
        System.out.println("MainView loaded");
        // addClassName("status-list-view");
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
        FlexLayout movieGrid = new FlexLayout();
        movieGrid.setFlexWrap(FlexWrap.WRAP);
        movieGrid.getStyle().set("gap", "1em");

        List<Movie> movies = movieRepository.findAll();
        for (Movie movie : movies) {
            VerticalLayout card = new VerticalLayout();
            card.getStyle().set("background", "rgba(30,30,30,0.85)");
            card.getStyle().set("padding", "1em");
            card.getStyle().set("borderRadius", "8px");
            card.getStyle().set("minWidth", "180px");
            card.getStyle().set("maxWidth", "200px");
            card.getStyle().set("margin", "0.5em");
            card.setAlignItems(Alignment.CENTER);
            card.getStyle().set("cursor", "pointer");
            card.addClassName("movie-card");

            Span title = new Span(movie.getTitle());
            card.add(title);

            card.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("movie-details/" + movie.getId())));

            movieGrid.add(card);
        }
        return movieGrid;
    }

    private void configureGrid(MovieRepository movieRepository) {
        List<Movie> movies = movieRepository.findAll();
        cinemaGrid.addColumn(i -> i).setHeader("Num");
        /// Image image = new Image("/images/spider.jpg", "alt message");
        // cinemaGrid.addComponentColumn(i -> image).setHeader("images");
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
