package com.finals.cinema.view;

import com.finals.cinema.model.entity.Movie;
import com.finals.cinema.model.entity.Projection;
import com.finals.cinema.model.entity.Cinema;
import com.finals.cinema.repository.MovieRepository;
import com.finals.cinema.repository.ProjectionRepository;
import com.finals.cinema.repository.CinemaRepository;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Route(value = "movie-details/:id", layout = MainLayout.class)
public class MovieDetailsView extends VerticalLayout implements BeforeEnterObserver {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ProjectionRepository projectionRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get("id").orElse(null);
        System.out.println("DEBUG: setParameter called with id = " + id);
        removeAll();
        setPadding(true);
        setSpacing(true);

        if (id != null) {
            try {
                int movieId = Integer.parseInt(id);
                Optional<Movie> movieOpt = movieRepository.findById(movieId);

                if (movieOpt.isPresent()) {
                    Movie movie = movieOpt.get();
                    buildMovieDetailsUI(movie);
                } else {
                    add(new H1("Movie not found."));
                }
            } catch (NumberFormatException e) {
                add(new H1("Invalid movie ID."));
            }
        } else {
            add(new H1("No movie ID provided."));
        }
    }

    private void buildMovieDetailsUI(Movie movie) {
        // Set dark background for entire page
        getStyle()
                .set("background-color", "#1a1a1a")
                .set("color", "#ffffff")
                .set("min-height", "100vh");
        
        // Trailer Section
        VerticalLayout trailerSection = createTrailerSection(movie);
        add(trailerSection);

        // Main content: Poster + Details in horizontal layout
        HorizontalLayout mainContentLayout = new HorizontalLayout();
        mainContentLayout.setWidthFull();
        mainContentLayout.setSpacing(true);
        mainContentLayout.setPadding(true);
        mainContentLayout.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto");

        // Poster section (left side)
        VerticalLayout posterSection = createPosterSection(movie);
        posterSection.setWidth("300px");
        posterSection.getStyle()
                .set("flex-shrink", "0");

        // Details section (right side)
        VerticalLayout detailsSection = createDetailsSection(movie);
        detailsSection.setWidthFull();

        mainContentLayout.add(posterSection, detailsSection);
        add(mainContentLayout);

        // Synopsis section
        VerticalLayout synopsisSection = createSynopsisSection(movie);
        add(synopsisSection);

        // Programme/Projections section
        VerticalLayout programmesSection = createProgrammesSection(movie);
        add(programmesSection);
    }

    private VerticalLayout createTrailerSection(Movie movie) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setPadding(true);
        layout.getStyle()
                .set("background-color", "#0a0a0a")
                .set("padding", "2em 0")
                .set("display", "flex")
                .set("align-items", "center");

        H1 title = new H1(movie.getTitle());
        title.getStyle()
                .set("color", "#ffffff")
                .set("margin", "0 0 1em 0")
                .set("text-align", "center");
        layout.add(title);

        if (movie.getTrailer() != null && !movie.getTrailer().isEmpty()) {
            String iframeHtml = "<iframe width='800' height='450' src='"
                    + movie.getTrailer() 
                    + "' frameborder='0' allow='accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture' allowfullscreen style='border-radius: 8px;'></iframe>";
            layout.add(new Html(iframeHtml));
        } else {
            Paragraph noTrailer = new Paragraph("No trailer available");
            noTrailer.getStyle().set("color", "#888888");
            layout.add(noTrailer);
        }

        return layout;
    }

    private VerticalLayout createPosterSection(Movie movie) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.getStyle()
                .set("display", "flex")
                .set("align-items", "center");

        if (movie.getPoster() != null && !movie.getPoster().isEmpty()) {
            Image poster = new Image(movie.getPoster(), "Movie Poster");
            poster.setWidth("280px");
            poster.setHeight("auto");
            poster.getStyle()
                    .set("border-radius", "8px")
                    .set("box-shadow", "0 4px 6px rgba(0,0,0,0.3)");
            layout.add(poster);
        } else {
            Div placeholder = new Div();
            placeholder.setText("No poster available");
            placeholder.setWidth("280px");
            placeholder.setHeight("400px");
            placeholder.getStyle()
                    .set("background-color", "#2a2a2a")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "center")
                    .set("color", "#888888")
                    .set("border-radius", "8px");
            layout.add(placeholder);
        }

        return layout;
    }

    private VerticalLayout createDetailsSection(Movie movie) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.getStyle()
                .set("background-color", "#2a2a2a")
                .set("border-radius", "8px")
                .set("padding", "1.5em");

        // Details grid layout
        Div detailsGrid = new Div();
        detailsGrid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "1fr 1fr")
                .set("gap", "1em");

        // Duration
        if (movie.getLength() > 0) {
            detailsGrid.add(createDetailItem("DURATION", movie.getLength() + " Minutes"));
        }

        // Premiere
        if (movie.getYear() != null && !movie.getYear().isEmpty()) {
            detailsGrid.add(createDetailItem("PREMIERE", movie.getYear()));
        }

        // Audio
        detailsGrid.add(createDetailItem("AUDIO", "Bulgarian"));

        // Genre
        if (movie.getGenre() != null) {
            detailsGrid.add(createDetailItem("GENRE", movie.getGenre().getType()));
        }

        // Category
        detailsGrid.add(createDetailItem("CATEGORY", "not for projections for children up to 12 years"));

        // Format
        detailsGrid.add(createDetailItem("FORMAT", "RELAX 2D"));

        layout.add(detailsGrid);

        // Credits section
        if (movie.getLeadingActor() != null && !movie.getLeadingActor().isEmpty()) {
            Div creditsSection = new Div();
            creditsSection.getStyle()
                    .set("margin-top", "1.5em")
                    .set("padding-top", "1em")
                    .set("border-top", "1px solid #3a3a3a");
            
            H4 creditsHeader = new H4("CREDITS");
            creditsHeader.getStyle()
                    .set("color", "#888888")
                    .set("font-size", "0.9em")
                    .set("margin", "0 0 0.5em 0");
            creditsSection.add(creditsHeader);

            Paragraph actors = new Paragraph("Actors: " + movie.getLeadingActor());
            actors.getStyle()
                    .set("color", "#cccccc")
                    .set("margin", "0");
            creditsSection.add(actors);
            
            layout.add(creditsSection);
        }

        return layout;
    }

    private Div createDetailItem(String label, String value) {
        Div item = new Div();
        
        Div labelDiv = new Div();
        labelDiv.setText(label);
        labelDiv.getStyle()
                .set("color", "#888888")
                .set("font-size", "0.85em")
                .set("margin-bottom", "0.25em")
                .set("font-weight", "600");
        
        Div valueDiv = new Div();
        valueDiv.setText(value);
        valueDiv.getStyle()
                .set("color", "#ffffff")
                .set("font-size", "0.95em");
        
        item.add(labelDiv, valueDiv);
        return item;
    }

    private VerticalLayout createSynopsisSection(Movie movie) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(true);
        layout.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto");

        if (movie.getPlot() != null && !movie.getPlot().isEmpty()) {
            H3 synopsisHeader = new H3("SYNOPSIS");
            synopsisHeader.getStyle()
                    .set("color", "#ffffff")
                    .set("margin", "0 0 1em 0")
                    .set("font-size", "1.2em");
            layout.add(synopsisHeader);

            Paragraph synopsis = new Paragraph(movie.getPlot());
            synopsis.getStyle()
                    .set("color", "#cccccc")
                    .set("line-height", "1.6")
                    .set("background-color", "#2a2a2a")
                    .set("padding", "1.5em")
                    .set("border-radius", "8px");
            layout.add(synopsis);
        }

        return layout;
    }

    private VerticalLayout createProgrammesSection(Movie movie) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto")
                .set("background-color", "#1a1a1a");

        H3 programmesHeader = new H3("PROGRAM");
        programmesHeader.getStyle()
                .set("color", "#ffffff")
                .set("font-size", "1.5em")
                .set("font-weight", "bold")
                .set("margin", "1em 0 1em 0")
                .set("text-transform", "uppercase");
        layout.add(programmesHeader);

        // Get all projections for this movie
        List<Projection> projections = projectionRepository.findAll().stream()
                .filter(p -> p.getMovie().getId().equals(movie.getId()))
                .collect(Collectors.toList());

        if (projections.isEmpty()) {
            Paragraph noProjections = new Paragraph("No projections available");
            noProjections.getStyle()
                    .set("color", "#888888")
                    .set("padding", "2em")
                    .set("text-align", "center");
            layout.add(noProjections);
            return layout;
        }

        // Group projections by date
        LocalDate today = LocalDate.now();
        List<LocalDate> uniqueDates = projections.stream()
                .map(p -> p.getStartAt().toLocalDate())
                .distinct()
                .filter(date -> !date.isBefore(today)) // Only show today and future dates
                .sorted()
                .limit(10) // Show max 10 days
                .collect(Collectors.toList());

        if (uniqueDates.isEmpty()) {
            Paragraph noProjections = new Paragraph("No upcoming projections");
            noProjections.getStyle()
                    .set("color", "#888888")
                    .set("padding", "2em")
                    .set("text-align", "center");
            layout.add(noProjections);
            return layout;
        }

        // Container for projections display (will be updated when date is selected)
        VerticalLayout projectionsContainer = new VerticalLayout();
        projectionsContainer.setWidthFull();
        projectionsContainer.setSpacing(true);

        // Create date buttons
        HorizontalLayout dateButtonsLayout = new HorizontalLayout();
        dateButtonsLayout.setWidthFull();
        dateButtonsLayout.setSpacing(true);
        dateButtonsLayout.getStyle()
                .set("overflow-x", "auto")
                .set("padding-bottom", "1em")
                .set("margin-bottom", "1.5em")
                .set("border-bottom", "2px solid #3a3a3a");

        LocalDate[] selectedDate = {uniqueDates.get(0)};

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM");

        for (LocalDate date : uniqueDates) {
            Button dateButton = new Button(
                    dayFormatter.format(date).toUpperCase() + "\n" +
                    dateFormatter.format(date)
            );
            dateButton.setWidth("90px");
            dateButton.setHeight("80px");
            dateButton.getStyle()
                    .set("white-space", "pre-wrap")
                    .set("font-weight", "600")
                    .set("font-size", "0.95em")
                    .set("border-radius", "8px")
                    .set("background-color", "#2a2a2a")
                    .set("color", "#ffffff")
                    .set("border", "2px solid #3a3a3a")
                    .set("cursor", "pointer")
                    .set("transition", "all 0.2s");

            // Mark first date as selected
            if (date.equals(selectedDate[0])) {
                dateButton.getStyle()
                        .set("background-color", "#d32f2f")
                        .set("border-color", "#d32f2f")
                        .set("color", "#ffffff");
            }

            LocalDate finalDate = date;
            dateButton.addClickListener(e -> {
                // Remove selection from all buttons
                dateButtonsLayout.getChildren()
                        .filter(Button.class::isInstance)
                        .forEach(btn -> {
                            Button b = (Button) btn;
                            b.getStyle()
                                    .set("background-color", "#2a2a2a")
                                    .set("border-color", "#3a3a3a");
                        });
                
                // Add selection to clicked button
                dateButton.getStyle()
                        .set("background-color", "#d32f2f")
                        .set("border-color", "#d32f2f");
                
                // Update projections display
                selectedDate[0] = finalDate;
                projectionsContainer.removeAll();
                projectionsContainer.add(createProjectionsForDate(projections, finalDate));
            });

            dateButtonsLayout.add(dateButton);
        }

        layout.add(dateButtonsLayout);

        // Display projections grouped by cinema for the selected date
        projectionsContainer.add(createProjectionsForDate(projections, selectedDate[0]));
        layout.add(projectionsContainer);

        return layout;
    }

    private VerticalLayout createProjectionsForDate(List<Projection> projections, LocalDate date) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);
        layout.setPadding(false);

        // Filter projections for the selected date
        List<Projection> dateProjections = projections.stream()
                .filter(p -> p.getStartAt().toLocalDate().equals(date))
                .collect(Collectors.toList());

        // Group by cinema
        List<Cinema> cinemas = cinemaRepository.findAll();

        for (Cinema cinema : cinemas) {
            List<Projection> cinemaProjections = dateProjections.stream()
                    .filter(p -> p.getHall().getCinema().getId().equals(cinema.getId()))
                    .collect(Collectors.toList());

            if (!cinemaProjections.isEmpty()) {
                // Create cinema section with dark theme
                VerticalLayout cinemaLayout = new VerticalLayout();
                cinemaLayout.getStyle()
                        .set("background-color", "#2a2a2a")
                        .set("padding", "1.5em")
                        .set("margin-bottom", "1em")
                        .set("border-radius", "8px")
                        .set("border-left", "4px solid #d32f2f");
                cinemaLayout.setSpacing(true);
                cinemaLayout.setWidthFull();
                cinemaLayout.setPadding(false);
                cinemaLayout.setMargin(false);

                // Cinema name
                H4 cinemaTitle = new H4(cinema.getName());
                cinemaTitle.getStyle()
                        .set("margin", "0 0 0.5em 0")
                        .set("color", "#ffffff")
                        .set("font-size", "1.1em")
                        .set("font-weight", "600");
                cinemaLayout.add(cinemaTitle);

                // Cinema info
                Span cinemaInfo = new Span(cinema.getCity());
                cinemaInfo.getStyle()
                        .set("color", "#888888")
                        .set("font-size", "0.9em");
                cinemaLayout.add(cinemaInfo);

                // Display projections as time buttons
                HorizontalLayout projectionsButtonsLayout = new HorizontalLayout();
                projectionsButtonsLayout.setSpacing(true);
                projectionsButtonsLayout.getStyle()
                        .set("flex-wrap", "wrap")
                        .set("margin-top", "1em");

                // Sort projections by time
                cinemaProjections.sort((p1, p2) -> p1.getStartAt().compareTo(p2.getStartAt()));

                for (Projection projection : cinemaProjections) {
                    String timeStr = projection.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm"));
                    Button projectionButton = new Button(timeStr);
                    projectionButton.setWidth("80px");
                    projectionButton.setHeight("45px");
                    projectionButton.getStyle()
                            .set("font-weight", "600")
                            .set("font-size", "1em")
                            .set("background-color", "#3a3a3a")
                            .set("color", "#ffffff")
                            .set("border", "1px solid #4a4a4a")
                            .set("border-radius", "6px")
                            .set("cursor", "pointer")
                            .set("transition", "all 0.2s");

                    // Hover effect
                    projectionButton.getElement().addEventListener("mouseenter", e -> {
                        projectionButton.getStyle()
                                .set("background-color", "#d32f2f")
                                .set("border-color", "#d32f2f");
                    });
                    projectionButton.getElement().addEventListener("mouseleave", e -> {
                        projectionButton.getStyle()
                                .set("background-color", "#3a3a3a")
                                .set("border-color", "#4a4a4a");
                    });

                    // Navigate to booking page when clicked
                    projectionButton.addClickListener(e -> {
                        getUI().ifPresent(ui -> ui.navigate("booking/" + projection.getId()));
                    });

                    projectionsButtonsLayout.add(projectionButton);
                }

                cinemaLayout.add(projectionsButtonsLayout);
                layout.add(cinemaLayout);
            }
        }

        // Message if no projections available for selected date
        if (dateProjections.isEmpty()) {
            Paragraph noProjections = new Paragraph("No projections available for this date");
            noProjections.getStyle()
                    .set("color", "#888888")
                    .set("padding", "2em")
                    .set("text-align", "center")
                    .set("background-color", "#2a2a2a")
                    .set("border-radius", "8px");
            layout.add(noProjections);
        }

        return layout;
    }
}