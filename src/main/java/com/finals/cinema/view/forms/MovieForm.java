package com.finals.cinema.view.forms;

import com.finals.cinema.model.DTO.AddMovieDTO;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class MovieForm extends FormLayout {
    TextField title = new TextField("Title");
    NumberField ageRestriction = new NumberField("Age Restriction");
    NumberField genreId = new NumberField("Genre ID");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private AddMovieDTO movie;

    public MovieForm() {
        addClassName("movie-form");

        title.setRequired(true);
        ageRestriction.setRequired(true);
        genreId.setRequired(true);

        add(title, ageRestriction, genreId, createButtonsLayout());
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

    public void setMovie(AddMovieDTO movie) {
        this.movie = movie;

        if (movie != null) {
            title.setValue(movie.getTitle() != null ? movie.getTitle() : "");
            ageRestriction.setValue(movie.getAgeRestriction() != null ? movie.getAgeRestriction().doubleValue() : null);
            genreId.setValue(movie.getGenreId() != null ? movie.getGenreId().doubleValue() : null);
        } else {
            title.clear();
            ageRestriction.clear();
            genreId.clear();
        }
    }

    public AddMovieDTO getMovieData() {
        return AddMovieDTO.builder()
                .title(title.getValue())
                .ageRestriction(ageRestriction.getValue().intValue())
                .genreId(genreId.getValue().intValue())
                .build();
    }

    // Events
    public static abstract class MovieFormEvent extends ComponentEvent<MovieForm> {
        private final AddMovieDTO movie;

        protected MovieFormEvent(MovieForm source, AddMovieDTO movie) {
            super(source, false);
            this.movie = movie;
        }

        public AddMovieDTO getMovie() {
            return movie;
        }
    }

    public static class SaveEvent extends MovieFormEvent {
        SaveEvent(MovieForm source, AddMovieDTO movie) {
            super(source, movie);
        }
    }

    public static class DeleteEvent extends MovieFormEvent {
        DeleteEvent(MovieForm source, AddMovieDTO movie) {
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
