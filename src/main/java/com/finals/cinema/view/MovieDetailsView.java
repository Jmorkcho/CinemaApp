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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        // 1st Tile - Trailer with link (half width)
        VerticalLayout trailerSection = createTrailerSection(movie);
        trailerSection.setWidth("100%");
        add(trailerSection);

        // 2nd, 3rd, 4th sections - Horizontal layout with poster and details (with
        // borders)
        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setWidthFull();
        contentLayout.setHeight("450px");
        contentLayout.setSpacing(true);
        contentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // 2nd section - Poster (1/3)
        VerticalLayout posterSection = createPosterSection(movie);
        posterSection.setWidth("15%");
        addBorder(posterSection);
        contentLayout.add(posterSection);

        // 3rd section - Basic details (1/3) - duration, premiere, audio, genre,
        // category
        VerticalLayout detailsSection1 = createDetailsSection1(movie);
        detailsSection1.setWidth("20%");
        addBorder(detailsSection1);
        contentLayout.add(detailsSection1);

        // 4th section - Extended details (1/3) - director, actors, format
        VerticalLayout detailsSection2 = createDetailsSection2(movie);
        detailsSection2.setWidth("20%");
        addBorder(detailsSection2);
        contentLayout.add(detailsSection2);

        add(contentLayout);

        HorizontalLayout descriptionLayout = new HorizontalLayout();
        descriptionLayout.setWidthFull();
        descriptionLayout.setHeight("200px");
        descriptionLayout.setSpacing(true);
        descriptionLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Div text = new Div();
        //TODO String pixels = detailsSection1.getWidth()+ detailsSection2.getWidth()+ posterSection.getWidth();
        text.setWidth("55.5%");
        text.setText(movie.getPlot());
        text.getStyle()
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("padding", "4px 8px");
        descriptionLayout.add(text);
        add(descriptionLayout);

        // Programme/Projections section
        VerticalLayout programmesSection = createProgrammesSection(movie);
        add(programmesSection);
    }

    private void addBorder(VerticalLayout layout) {
        layout.getElement().getStyle().set("border", "1px solid #ccc");
        layout.getElement().getStyle().set("border-left", "1px solid #ccc");
    }

    private VerticalLayout createTrailerSection(Movie movie) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H1 title = new H1(movie.getTitle());
        layout.add(title);

        if (movie.getTrailer() != null && !movie.getTrailer().isEmpty()) {
            String iframeHtml = "<div style='display: flex; justify-content: center;'><iframe style='display:block' width='560' height='315' src='"
                    + movie.getTrailer() + "' frameborder='0' allowfullscreen></iframe></div>";
            layout.add(new Html(iframeHtml));
        } else {
            layout.add(new Paragraph("No trailer available"));
            System.out.println("DEBUG: No trailer - checking trailer " + movie.getTrailer());
        }

        return layout;
    }

    private VerticalLayout createPosterSection(Movie movie) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(true);
        layout.setSizeFull();

        // layout.setAlignItems(Alignment.STRETCH);

        if (movie.getPoster() != null && !movie.getPoster().isEmpty()) {
            Image poster = new Image(movie.getPoster(), "Movie Poster");
            // poster.setSizeFull();
            // poster.getStyle().set("object-fit", "cover");
            // poster.getStyle().set("height", "60%");
            // poster.getStyle().set("width", "60%");
            layout.add(poster);
            // layout.setFlexGrow(1, poster);
            layout.expand(poster);
            layout.setAlignItems(Alignment.CENTER);
        } else {
            layout.add(new Paragraph("No poster available"));
        }

        return layout;
    }

    private VerticalLayout createDetailsSection1(Movie movie) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);

        H3 detailsHeader = new H3("Details");
        layout.add(detailsHeader);

        if (movie.getLength() > 0) {
            layout.add(new Paragraph("Duration: " + movie.getLength() + " min"));
        }

        if (movie.getYear() != null && !movie.getYear().isEmpty()) {
            layout.add(new Paragraph("Premiere: " + movie.getYear()));
        }

        layout.add(new Paragraph("Audio: Not specified"));

        if (movie.getGenre() != null) {
            layout.add(new Paragraph("Genre: " + movie.getGenre().getType()));
        }

        layout.add(new Paragraph("Category: PG-13"));

        return layout;
    }

    private VerticalLayout createDetailsSection2(Movie movie) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);

        H3 creditsHeader = new H3("Credits");
        layout.add(creditsHeader);

        layout.add(new Paragraph("Director: Not specified"));

        if (movie.getLeadingActor() != null && !movie.getLeadingActor().isEmpty()) {
            layout.add(new Paragraph("Actors: " + movie.getLeadingActor()));
        }

        layout.add(new Paragraph("Format: 3D IMAX 3D"));

        if (movie.getRating() != null) {
            layout.add(new Paragraph("Rating: " + movie.getRating()));
        }

        return layout;
    }

    private VerticalLayout createProgrammesSection(Movie movie) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 programmesHeader = new H3("Programme");
        layout.add(programmesHeader);

        // Get all projections for this movie
        List<Projection> projections = projectionRepository.findAll().stream()
                .filter(p -> p.getMovie().getId().equals(movie.getId()))
                .collect(Collectors.toList());

        if (projections.isEmpty()) {
            layout.add(new Paragraph("No projections available"));
            return layout;
        }

        // Group projections by date
        LocalDate today = LocalDate.now();
        List<LocalDate> uniqueDates = projections.stream()
                .map(p -> p.getStartAt().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Create date buttons
        HorizontalLayout dateButtonsLayout = new HorizontalLayout();
        dateButtonsLayout.setSpacing(true);

        LocalDate selectedDate = today;
        for (LocalDate date : uniqueDates) {
            Button dateButton = new Button(date.format(DateTimeFormatter.ofPattern("dd.MM")));
            dateButton.setWidth("80px");
            dateButtonsLayout.add(dateButton);
        }

        layout.add(dateButtonsLayout);

        // Display projections grouped by cinema for the first date
        if (!uniqueDates.isEmpty()) {
            selectedDate = uniqueDates.get(0);
            VerticalLayout projectionsLayout = createProjectionsForDate(projections, selectedDate);
            layout.add(projectionsLayout);
        }

        return layout;
    }

    private VerticalLayout createProjectionsForDate(List<Projection> projections, LocalDate date) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);

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
                // Create cinema section
                VerticalLayout cinemaLayout = new VerticalLayout();
                cinemaLayout.getElement().getStyle().set("border", "1px solid #ddd");
                cinemaLayout.setPadding(true);
                cinemaLayout.setSpacing(true);
                cinemaLayout.setWidthFull();

                H3 cinemaTitle = new H3(cinema.getName() + " - " + cinema.getCity());
                cinemaLayout.add(cinemaTitle);

                // Display projections as buttons
                HorizontalLayout projectionsButtonsLayout = new HorizontalLayout();
                projectionsButtonsLayout.setSpacing(true);
                projectionsButtonsLayout.getElement().getStyle().set("flex-wrap", "wrap");

                for (Projection projection : cinemaProjections) {
                    String timeStr = projection.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm"));
                    String hallInfo = "Hall " + projection.getHall().getNumber();
                    Button projectionButton = new Button(timeStr + "\n" + hallInfo);
                    projectionButton.setWidth("100px");
                    projectionButton.setHeight("80px");
                    projectionsButtonsLayout.add(projectionButton);
                }

                cinemaLayout.add(projectionsButtonsLayout);
                layout.add(cinemaLayout);
            }
        }

        return layout;
    }
}