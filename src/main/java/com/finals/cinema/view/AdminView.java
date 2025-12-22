package com.finals.cinema.view;

import com.finals.cinema.model.DTO.AddMovieDTO;
import com.finals.cinema.model.DTO.MovieDTO;
import com.finals.cinema.model.DTO.ResponseMovieDTO;
import com.finals.cinema.model.DTO.UpdateMovieDTO;
import com.finals.cinema.model.entity.Genre;
import com.finals.cinema.model.entity.User;
import com.finals.cinema.model.entity.UserRole;
import com.finals.cinema.service.GenreService;
import com.finals.cinema.service.MovieService;
import com.finals.cinema.service.UserService;
import com.finals.cinema.view.forms.MovieForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;

import java.util.Map;
import java.util.stream.Collectors;


@Secured("ROLE_ADMIN")
@Route(value = "admin_panel", layout = MainLayout.class)
@Slf4j
public class AdminView extends VerticalLayout implements BeforeEnterObserver {

    private final MovieService movieService;
    private final UserService userService;
    private final GenreService genreService;

    public AdminView(MovieService movieService, UserService userService, GenreService genreService) {
        this.movieService = movieService;
        this.userService = userService;
        this.genreService = genreService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Create tabs
        Tab usersTab = new Tab("Users");
        Tab moviesTab = new Tab("Movies");
        Tab projectionsTab = new Tab("Projections");
        Tabs tabs = new Tabs(usersTab, moviesTab, projectionsTab);

        // Create content for each tab
        VerticalLayout usersContent = createUsersContent();
        VerticalLayout moviesContent = createMoviesContent();
        VerticalLayout projectionsContent = createProjectionsContent();

        // Show content based on selected tab
        tabs.addSelectedChangeListener(event -> {
            usersContent.setVisible(event.getSelectedTab() == usersTab);
            moviesContent.setVisible(event.getSelectedTab() == moviesTab);
            projectionsContent.setVisible(event.getSelectedTab() == projectionsTab);
        });

        // Default to showing users
        usersContent.setVisible(true);
        moviesContent.setVisible(false);
        projectionsContent.setVisible(false);

        add(tabs, usersContent, moviesContent, projectionsContent);
    }

    private VerticalLayout createUsersContent() {
        VerticalLayout layout = new VerticalLayout();

        // User grid
        Grid<User> userGrid = new Grid<>(User.class);
        userGrid.setItems(userService.findAll());
        userGrid.setColumns("username", "email", "firstName", "lastName", "role");
        userGrid.getColumnByKey("role").setHeader("Role");

        // Delete button with confirmation dialog
        Button deleteUserButton = new Button("Delete", e -> {
            User selected = userGrid.asSingleSelect().getValue();
            //check if admin todo
            if (selected != null) {
                showDeleteConfirmationDialog(selected, userGrid);
            }
        });

        Button makeAdminButton = new Button("Make Admin", e -> {
            User selected = userGrid.asSingleSelect().getValue();
            if (selected != null) {
                userService.changeUserRole(selected.getId(), UserRole.ADMIN);
                userGrid.setItems(userService.findAll());
            }
        });

        layout.add(userGrid, new HorizontalLayout(deleteUserButton, makeAdminButton));
        return layout;
    }

    private void showDeleteConfirmationDialog(User user, Grid<User> userGrid) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        // Create components for the dialog
        TextField confirmationField = new TextField("Type 'delete forever' to confirm");
        confirmationField.setWidthFull();

        Button confirmButton = new Button("Delete", event -> {
            if ("delete forever".equalsIgnoreCase(confirmationField.getValue())) {
                try {
                    userService.deleteUser(user.getId());
                } catch (Exception e) {
                    log.error("Could not delete user");
                }
                userGrid.setItems(userService.findAll());
                dialog.close();
            } else {
                confirmationField.setInvalid(true);
                confirmationField.setErrorMessage("Text doesn't match");
            }
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Layout for buttons
        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        // Add components to dialog
        dialog.add(
                new H3("Delete User: " + user.getUsername()),
                new Paragraph("This action is irreversible. All user data will be permanently deleted."),
                confirmationField,
                buttonLayout
        );

        // Open the dialog
        dialog.open();
    }

    private VerticalLayout createMoviesContent() {
        VerticalLayout layout = new VerticalLayout();

        Grid<ResponseMovieDTO> movieGrid = new Grid<>(ResponseMovieDTO.class);
        movieGrid.setColumns("id", "title", "year", "genreName", "plot", "length", "rating", "ageRestriction", "leadingActor", "poster", "imdbId");

       MovieForm form = new MovieForm(genreService);
        form.setWidth("25em");
        form.setVisible(false);

        HorizontalLayout content = new HorizontalLayout(movieGrid, form);
        content.setFlexGrow(2, movieGrid);
        content.setFlexGrow(1, form);
        content.setSizeFull();

        refreshMovieGrid(movieGrid);

        Button addMovieButton = new Button("Add");
        Button deleteMovieButton = new Button("Delete");

        deleteMovieButton.setEnabled(false);

        movieGrid.asSingleSelect().addValueChangeListener(event -> {
            ResponseMovieDTO selected = event.getValue();

            if (selected != null) {
                MovieDTO movie = MovieDTO.builder()
                  .id(selected.getId())
                  .title(selected.getTitle())
                  .ageRestriction(selected.getAgeRestriction())
                  .genre(selected.getGenre().getType())
                  .build();
                form.setMovie(movie);
            deleteMovieButton.setEnabled(true);
            form.setVisible(true);
            } else {
                form.setVisible(false);
            }
        });

        addMovieButton.addClickListener(e -> {
            movieGrid.asSingleSelect().clear();
            form.setMovie(MovieDTO.builder().build());
            form.setVisible(true);
        });

        deleteMovieButton.addClickListener(event -> {
            ResponseMovieDTO selectedMovie = movieGrid.asSingleSelect().getValue();
            if (selectedMovie != null) {
                try {
                    movieService.deleteMovie(selectedMovie.getId());
                    refreshMovieGrid(movieGrid);
                    form.setVisible(false);
                } catch (Exception ex) {
                    log.error("Could not delete movie", ex);
                }
            }
        });

        form.addListener(MovieForm.SaveEvent.class, event -> {
            try {
                UpdateMovieDTO movie = form.getMovieData();
                movieService.updateMovie(movie);
                refreshMovieGrid(movieGrid);
                form.setVisible(false);
            } catch (Exception ex) {
                log.error("Could not add movie", ex);
            }
        });

        form.addListener(MovieForm.DeleteEvent.class, event -> {
            ResponseMovieDTO selectedMovie = movieGrid.asSingleSelect().getValue();
            if (selectedMovie != null) {
                try {
                    movieService.deleteMovie(selectedMovie.getId());
                    refreshMovieGrid(movieGrid);
                    form.setVisible(false);
                } catch (Exception ex) {
                    log.error("Could not delete movie", ex);
                }
            }
        });

        form.addListener(MovieForm.CloseEvent.class, e -> form.setVisible(false));

        HorizontalLayout buttonLayout = new HorizontalLayout(addMovieButton, deleteMovieButton);
        layout.add(content, buttonLayout);
        return layout;
    }



    private void refreshMovieGrid(Grid<ResponseMovieDTO> grid) {
        grid.setItems(movieService.getAllMovies());
    }

    private void showMovieDialog(ResponseMovieDTO movie, Grid<ResponseMovieDTO> movieGrid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(movie == null ? "Add Movie" : "Edit Movie");

        TextField title = new TextField("Title");
        NumberField ageRestriction = new NumberField("Age Restriction");
        ComboBox<String> genre = new ComboBox<>("Genre");

        if (movie != null) {
            title.setValue(movie.getTitle());
            ageRestriction.setValue((double) movie.getAgeRestriction());
            genre.setValue(movie.getGenre().getType());
        }

        Button save = new Button("Save", event -> {
            try {
                Map<String, Genre> genreTypesToGenre = genreService.getAllGenres()
                  .stream()
                  .collect(Collectors.toMap(Genre::getType, item -> item));

                AddMovieDTO dto = AddMovieDTO.builder()
                        .title(title.getValue())
                        .ageRestriction(ageRestriction.getValue().intValue())
                        .genre(genreTypesToGenre.get(genre.getValue()))
                        .build();

                if (movie == null) {
                    movieService.addMovie(dto); // Assume admin
                } else {
                    movieService.deleteMovie(movie.getId());
                    movieService.addMovie(dto);
                }

                refreshMovieGrid(movieGrid);
                dialog.close();
            } catch (Exception ex) {
                log.error("Could not save movie", ex);
            }
        });

        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.add(new FormLayout(title, ageRestriction, genre),
                new HorizontalLayout(save, cancel));
        dialog.open();
    }


    private void showMovieDeleteDialog(ResponseMovieDTO movie, Grid<ResponseMovieDTO> movieGrid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirm Delete");

        Paragraph warning = new Paragraph("Delete movie '" + movie.getTitle() + "'? This cannot be undone.");

        Button confirm = new Button("Delete", e -> {
            try {
                movieService.deleteMovie(movie.getId()); // admin id
                refreshMovieGrid(movieGrid);
            } catch (Exception ex) {
                log.error("Could not delete movie", ex);
            }
            dialog.close();
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.add(warning, new HorizontalLayout(confirm, cancel));
        dialog.open();
    }


    private VerticalLayout createProjectionsContent() {
        VerticalLayout layout = new VerticalLayout();
        // Similar to movies content
        // Add projection form and grid
        return layout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            if (UserRole.ADMIN != userService.getCurrentUserRole()) {
                event.forwardTo(AccessDeniedView.class);
            }
        } catch (Exception e) {
            event.forwardTo(AccessDeniedView.class);
        }
    }
}