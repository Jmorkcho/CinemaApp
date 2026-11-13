package com.finals.cinema.view;

import com.finals.cinema.model.DTO.AddMovieDTO;
import com.finals.cinema.model.DTO.MovieDTO;
import com.finals.cinema.model.DTO.RequestProjectionDTO;
import com.finals.cinema.model.DTO.ResponseMovieDTO;
import com.finals.cinema.model.DTO.UpdateMovieDTO;
import com.finals.cinema.model.entity.Genre;
import com.finals.cinema.model.entity.Hall;
import com.finals.cinema.model.entity.Movie;
import com.finals.cinema.model.entity.Projection;
import com.finals.cinema.model.entity.User;
import com.finals.cinema.model.entity.UserRole;
import com.finals.cinema.repository.HallRepository;
import com.finals.cinema.repository.MovieRepository;
import com.finals.cinema.service.GenreService;
import com.finals.cinema.service.MovieService;
import com.finals.cinema.service.ProjectionService;
import com.finals.cinema.service.UserService;
import com.finals.cinema.view.forms.MovieForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;


@Secured("ROLE_ADMIN")
@Route(value = "admin_panel", layout = MainLayout.class)
@Slf4j
public class AdminView extends VerticalLayout implements BeforeEnterObserver {

    private final MovieService movieService;
    private final UserService userService;
    private final GenreService genreService;
    private final ProjectionService projectionService;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;

    public AdminView(MovieService movieService, UserService userService, GenreService genreService,
                     ProjectionService projectionService, MovieRepository movieRepository,
                     HallRepository hallRepository) {
        this.projectionService = projectionService;
        this.movieRepository = movieRepository;
        this.hallRepository = hallRepository;
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


    private static final DateTimeFormatter PROJECTION_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private VerticalLayout createProjectionsContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Grid<Projection> grid = new Grid<>(Projection.class, false);
        grid.addColumn(Projection::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(p -> p.getMovie() == null ? "" : p.getMovie().getTitle()).setHeader("Movie").setSortable(true);
        grid.addColumn(p -> p.getHall() == null ? "" : p.getHall().getCinema().getName()).setHeader("Cinema").setSortable(true);
        grid.addColumn(p -> p.getHall() == null ? "" : "Hall " + p.getHall().getNumber()).setHeader("Hall");
        grid.addColumn(p -> PROJECTION_TIME.format(p.getStartAt())).setHeader("Starts")
                .setComparator(Projection::getStartAt).setSortable(true);
        grid.addColumn(p -> PROJECTION_TIME.format(p.getEndAt())).setHeader("Ends");
        grid.setSizeFull();
        grid.setItems(projectionService.getAllProjections());

        Runnable refresh = () -> grid.setItems(projectionService.getAllProjections());

        Button addButton = new Button("Add", e -> showProjectionDialog(null, refresh));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button editButton = new Button("Edit", e ->
                grid.asSingleSelect().getOptionalValue().ifPresent(p -> showProjectionDialog(p, refresh)));
        Button deleteButton = new Button("Delete", e ->
                grid.asSingleSelect().getOptionalValue().ifPresent(p -> showProjectionDeleteDialog(p, refresh)));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        grid.asSingleSelect().addValueChangeListener(e -> {
            editButton.setEnabled(e.getValue() != null);
            deleteButton.setEnabled(e.getValue() != null);
        });
        grid.addItemDoubleClickListener(e -> showProjectionDialog(e.getItem(), refresh));

        layout.add(grid, new HorizontalLayout(addButton, editButton, deleteButton));
        return layout;
    }

    private void showProjectionDialog(Projection projection, Runnable refresh) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(projection == null ? "Add Projection" : "Edit Projection");

        ComboBox<Movie> movie = new ComboBox<>("Movie");
        movie.setItems(movieRepository.findAll());
        movie.setItemLabelGenerator(Movie::getTitle);
        movie.setRequired(true);

        ComboBox<Hall> hall = new ComboBox<>("Hall");
        hall.setItems(hallRepository.findAll());
        hall.setItemLabelGenerator(h -> h.getCinema().getName() + " - Hall " + h.getNumber());
        hall.setRequired(true);

        DateTimePicker start = new DateTimePicker("Starts at");
        start.setRequiredIndicatorVisible(true);

        if (projection != null) {
            movie.setValue(projection.getMovie());
            hall.setValue(projection.getHall());
            start.setValue(projection.getStartAt());
        } else {
            start.setValue(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0));
        }

        Button save = new Button("Save", e -> {
            if (movie.getValue() == null || hall.getValue() == null || start.getValue() == null) {
                Notification.show("Please fill all fields").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            try {
                RequestProjectionDTO dto = new RequestProjectionDTO();
                dto.setMovieId(movie.getValue().getId());
                dto.setHallId(hall.getValue().getId());
                dto.setStartAt(start.getValue());
                if (projection == null) {
                    projectionService.addProjection(dto);
                } else {
                    projectionService.editProjection(dto, projection.getId());
                }
                refresh.run();
                dialog.close();
            } catch (Exception ex) {
                log.error("Could not save projection", ex);
                Notification.show(ex.getMessage() == null ? "Could not save projection" : ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        FormLayout form = new FormLayout(movie, hall, start);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        dialog.add(form);
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }

    private void showProjectionDeleteDialog(Projection projection, Runnable refresh) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirm Delete");
        dialog.add(new Paragraph("Delete the projection of '" + projection.getMovie().getTitle() + "' on "
                + PROJECTION_TIME.format(projection.getStartAt()) + "? This cannot be undone."));

        Button confirm = new Button("Delete", e -> {
            try {
                projectionService.deleteProjection(projection.getId());
                refresh.run();
                dialog.close();
            } catch (Exception ex) {
                log.error("Could not delete projection", ex);
                Notification.show(ex.getMessage() == null ? "Could not delete projection" : ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                dialog.close();
            }
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancel, confirm);
        dialog.open();
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