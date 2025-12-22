package com.finals.cinema.view.forms;

import com.finals.cinema.model.DTO.AddMovieDTO;
import com.finals.cinema.model.DTO.MovieDTO;
import com.finals.cinema.model.entity.Genre;
import com.finals.cinema.repository.MovieRepository;
import com.finals.cinema.service.GenreService;
import com.finals.cinema.service.MovieService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieForm extends FormLayout {

    private final GenreService genreService;

    TextField title = new TextField("Title");
    NumberField ageRestriction = new NumberField("Age Restriction");
    ComboBox<String> genreCombobox = new ComboBox<>("Genre");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private MovieDTO movie;

    public MovieForm(GenreService genreService) {
        this.genreService = genreService;
        addClassName("movie-form");

        title.setRequired(true);
        ageRestriction.setRequired(true);

        Map<String, Genre> genreTypesToGenre = genreService.getAllGenres()
          .stream()
          .collect(Collectors.toMap(Genre::getType, item -> item));
        genreCombobox.setItems(genreTypesToGenre.keySet());
        genreCombobox.setRequired(true);

        add(title, ageRestriction, genreCombobox, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> fireEvent(new SaveEvent(this, movie)));
        delete.addClickListener(e -> fireEvent(new DeleteEvent(this, movie)));
        close.addClickListener(e -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, close);
    }

    public void setMovie(MovieDTO movie) {
        this.movie = movie;

        if (movie != null) {
            title.setValue(movie.getTitle() != null ? movie.getTitle() : "");
            ageRestriction.setValue(movie.getAgeRestriction() != null ? movie.getAgeRestriction().doubleValue() : null);
            genreCombobox.setValue(movie.getGenre() != null ? movie.getGenre() : null);
        } else {
            title.clear();
            ageRestriction.clear();
            genreCombobox.clear();
        }
    }

    public AddMovieDTO getMovieData() {
        Map<String, Genre> genreTypesToGenre = genreService.getAllGenres()
          .stream()
          .collect(Collectors.toMap(Genre::getType, item -> item));

        return AddMovieDTO.builder()
                .title(title.getValue())
                .ageRestriction(ageRestriction.getValue().intValue())
                .genre(genreTypesToGenre.get(genreCombobox.getValue()))
                .build();
    }

    // Events
    public static abstract class MovieFormEvent extends ComponentEvent<MovieForm> {
        private final MovieDTO movie;

        protected MovieFormEvent(MovieForm source, MovieDTO movie) {
            super(source, false);
            this.movie = movie;
        }

        public MovieDTO getMovie() {
            return movie;
        }
    }

    public static class SaveEvent extends MovieFormEvent {
        SaveEvent(MovieForm source, MovieDTO movie) {
            super(source, movie);
        }
    }

    public static class DeleteEvent extends MovieFormEvent {
        DeleteEvent(MovieForm source, MovieDTO movie) {
            super(source, movie);
        }
    }

    public static class CloseEvent extends MovieFormEvent {
        CloseEvent(MovieForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
