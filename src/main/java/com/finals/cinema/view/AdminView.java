package com.finals.cinema.view;

import com.finals.cinema.model.DTO.AddMovieDTO;
import com.finals.cinema.model.DTO.ResponseMovieDTO;
import com.finals.cinema.model.entity.Genre;
import com.finals.cinema.model.entity.User;
import com.finals.cinema.service.MovieService;
import com.finals.cinema.service.UserService;
import com.finals.cinema.util.exceptions.UnauthorizedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import org.springframework.security.access.annotation.Secured;

import static com.finals.cinema.util.Constants.ROLE_ADMIN;


@Secured("ROLE_ADMIN")
@Route(value = "admin_panel", layout = MainLayout.class)
public class AdminView extends VerticalLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            if (userService.getCurrentUserRole() != ROLE_ADMIN) {
                event.forwardTo(AccessDeniedView.class);
            }
        } catch (Exception e) {
            event.forwardTo(AccessDeniedView.class);
        }
    }

    private final MovieService movieService;
    private final UserService userService;

    public AdminView(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;

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
        userGrid.setColumns("username", "email", "firstName", "lastName", "roleId");
        userGrid.getColumnByKey("roleId").setHeader("Role");

        // Delete button with confirmation dialog
        Button deleteButton = new Button("Delete", e -> {
            User selected = userGrid.asSingleSelect().getValue();
            if (selected != null) {
                showDeleteConfirmationDialog(selected, userGrid);
            }
        });

        Button makeAdminButton = new Button("Make Admin", e -> {
            User selected = userGrid.asSingleSelect().getValue();
            if (selected != null) {
                userService.changeUserRole(selected.getId(), 2);
                userGrid.setItems(userService.findAll());
            }
        });

        layout.add(userGrid, new HorizontalLayout(deleteButton, makeAdminButton));
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
                } catch (UnauthorizedException e) {
                    e.printStackTrace();
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
        movieGrid.setColumns("id", "title", "year", "plot", "length", "rating", "ageRestriction", "leadingActor", "poster", "imdb_id");

        com.finals.cinema.view.forms.MovieForm form = new com.finals.cinema.view.forms.MovieForm();
        form.setWidth("25em");
        form.setVisible(false);

        HorizontalLayout content = new HorizontalLayout(movieGrid, form);
        content.setFlexGrow(2, movieGrid);
        content.setFlexGrow(1, form);
        content.setSizeFull();

        refreshMovieGrid(movieGrid);

        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");

        deleteButton.setEnabled(false);

        movieGrid.asSingleSelect().addValueChangeListener(event -> {
            ResponseMovieDTO selected = event.getValue();
            deleteButton.setEnabled(selected != null);

            if (selected != null) {
                form.setMovie(
                        AddMovieDTO.builder()
                                .title(selected.getTitle())
                                .ageRestriction(selected.getAgeRestriction())
                                .genreId(selected.getGenre().getId())
                                .build()
                );
                form.setVisible(true);
            } else {
                form.setVisible(false);
            }
        });

        addButton.addClickListener(e -> {
            movieGrid.asSingleSelect().clear();
            form.setMovie(AddMovieDTO.builder().build());
            form.setVisible(true);
        });

        deleteButton.addClickListener(e -> {
            ResponseMovieDTO selected = movieGrid.asSingleSelect().getValue();
            if (selected != null) {
                try {
                    movieService.deleteMovie(selected.getId(), 2);
                    refreshMovieGrid(movieGrid);
                    form.setVisible(false);
                } catch (Exception | UnauthorizedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        form.addListener(com.finals.cinema.view.forms.MovieForm.SaveEvent.class, e -> {
            try {
                AddMovieDTO movie = form.getMovieData();
                movieService.addMovie(movie, 2);
                refreshMovieGrid(movieGrid);
                form.setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            } catch (UnauthorizedException ex) {
                ex.printStackTrace();
            }
        });

        form.addListener(com.finals.cinema.view.forms.MovieForm.DeleteEvent.class, e -> {
            ResponseMovieDTO selected = movieGrid.asSingleSelect().getValue();
            if (selected != null) {
                try {
                    movieService.deleteMovie(selected.getId(), 2);
                    refreshMovieGrid(movieGrid);
                    form.setVisible(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } catch (UnauthorizedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        form.addListener(com.finals.cinema.view.forms.MovieForm.CloseEvent.class, e -> form.setVisible(false));

        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, deleteButton);
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
        NumberField genreId = new NumberField("Genre ID");

        if (movie != null) {
            title.setValue(movie.getTitle());
            ageRestriction.setValue((double) movie.getAgeRestriction());
            genreId.setValue((double) movie.getGenre().getId());
        }

        Button save = new Button("Save", event -> {
            try {
                AddMovieDTO dto = AddMovieDTO.builder()
                        .title(title.getValue())
                        .ageRestriction(ageRestriction.getValue().intValue())
                        .genreId(genreId.getValue().intValue())
                        .build();

                if (movie == null) {
                    movieService.addMovie(dto, 2); // Assume admin
                } else {
                    movieService.deleteMovie(movie.getId(), 2);
                    movieService.addMovie(dto, 2);
                }

                refreshMovieGrid(movieGrid);
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } catch (UnauthorizedException e) {
                e.printStackTrace();
            }
        });

        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.add(new FormLayout(title, ageRestriction, genreId),
                new HorizontalLayout(save, cancel));
        dialog.open();
    }


    private void showMovieDeleteDialog(ResponseMovieDTO movie, Grid<ResponseMovieDTO> movieGrid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirm Delete");

        Paragraph warning = new Paragraph("Delete movie '" + movie.getTitle() + "'? This cannot be undone.");

        Button confirm = new Button("Delete", e -> {
            try {
                movieService.deleteMovie(movie.getId(), 2); // admin id
                refreshMovieGrid(movieGrid);
            } catch (Exception ex) {
                ex.printStackTrace();
            } catch (UnauthorizedException ex) {
                ex.printStackTrace();
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
}