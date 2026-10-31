package com.finals.cinema.view.components;

import com.finals.cinema.model.entity.Movie;
import com.finals.cinema.model.entity.Projection;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Lists the upcoming projections of a single cinema: a date strip on top and,
 * for the selected day, every movie with its showtimes.
 */
public class CinemaProgramme extends VerticalLayout {

    private static final int MAX_DAYS = 7;
    private static final Locale BG = Locale.forLanguageTag("bg");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final Map<LocalDate, List<Projection>> projectionsByDate = new TreeMap<>();
    private final Div moviesContainer = new Div();
    private final FlexLayout dateStrip = new FlexLayout();
    private final Map<LocalDate, Button> dateButtons = new LinkedHashMap<>();

    public CinemaProgramme(List<Projection> upcomingProjections) {
        setPadding(false);
        setSpacing(true);

        projectionsByDate.putAll(upcomingProjections.stream()
                .collect(Collectors.groupingBy(p -> p.getStartAt().toLocalDate())));

        if (projectionsByDate.isEmpty()) {
            add(new Paragraph("В момента няма предстоящи прожекции за това кино."));
            return;
        }

        dateStrip.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        dateStrip.getStyle().set("gap", "0.5em");

        projectionsByDate.keySet().stream().limit(MAX_DAYS).forEach(date -> {
            Button button = new Button(formatDate(date), e -> showDate(date));
            button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            dateButtons.put(date, button);
            dateStrip.add(button);
        });

        moviesContainer.setWidthFull();
        add(dateStrip, moviesContainer);
        showDate(dateButtons.keySet().iterator().next());
    }

    private static String formatDate(LocalDate date) {
        String day = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, BG).toUpperCase(BG);
        return day + " " + date.format(DateTimeFormatter.ofPattern("dd.MM"));
    }

    private void showDate(LocalDate date) {
        dateButtons.forEach((d, button) -> {
            if (d.equals(date)) {
                button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            } else {
                button.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            }
        });

        moviesContainer.removeAll();

        Map<Movie, List<Projection>> byMovie = projectionsByDate.get(date).stream()
                .collect(Collectors.groupingBy(Projection::getMovie));

        byMovie.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getTitle()))
                .forEach(entry -> moviesContainer.add(createMovieRow(entry.getKey(), entry.getValue())));
    }

    private Div createMovieRow(Movie movie, List<Projection> projections) {
        Div row = new Div();
        row.getStyle()
                .set("display", "flex")
                .set("gap", "1.5em")
                .set("padding", "1em 0")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        Div poster = createPoster(movie);

        Div info = new Div();
        info.getStyle().set("flex", "1").set("min-width", "0");

        H3 title = new H3(movie.getTitle());
        title.getStyle().set("margin", "0 0 0.25em 0").set("cursor", "pointer");
        title.addClickListener(e -> navigate("movie-details/" + movie.getId()));

        Span meta = new Span(buildMeta(movie));
        meta.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        FlexLayout times = new FlexLayout();
        times.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        times.getStyle().set("gap", "0.75em").set("margin-top", "1em");
        projections.stream()
                .sorted(Comparator.comparing(Projection::getStartAt))
                .forEach(p -> times.add(createTimeChip(p)));

        info.add(title, meta, times);
        row.add(poster, info);
        return row;
    }

    private Div createPoster(Movie movie) {
        Div wrapper = new Div();
        wrapper.getStyle()
                .set("width", "110px")
                .set("min-width", "110px")
                .set("height", "160px")
                .set("border-radius", "6px")
                .set("overflow", "hidden")
                .set("cursor", "pointer")
                .set("background", "var(--lumo-contrast-10pct)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        if (movie.getPoster() != null && !movie.getPoster().isBlank()) {
            Image image = new Image(movie.getPoster(), movie.getTitle());
            image.setWidth("100%");
            image.setHeight("100%");
            image.getStyle().set("object-fit", "cover");
            wrapper.add(image);
        } else {
            wrapper.add(new Span("Няма постер"));
        }
        wrapper.addClickListener(e -> navigate("movie-details/" + movie.getId()));
        return wrapper;
    }

    private Div createTimeChip(Projection projection) {
        Div chip = new Div();
        chip.addClassName("time-chip");

        Span time = new Span(projection.getStartAt().format(TIME_FORMAT));
        time.getStyle().set("font-weight", "700");

        Span hall = new Span("Зала " + projection.getHall().getNumber());
        hall.getStyle()
                .set("display", "block")
                .set("font-size", "var(--lumo-font-size-xxs)")
                .set("color", "var(--lumo-secondary-text-color)");

        chip.add(time, hall);
        chip.addClickListener(e -> navigate("booking/" + projection.getId()));
        return chip;
    }

    private String buildMeta(Movie movie) {
        StringBuilder meta = new StringBuilder();
        if (movie.getGenre() != null) {
            meta.append(movie.getGenre().getType());
        }
        if (movie.getLength() > 0) {
            appendSeparator(meta).append(movie.getLength()).append(" мин");
        }
        if (movie.getAgeRestriction() > 0) {
            appendSeparator(meta).append(movie.getAgeRestriction()).append("+");
        }
        return meta.toString();
    }

    private StringBuilder appendSeparator(StringBuilder builder) {
        return builder.length() > 0 ? builder.append(" • ") : builder;
    }

    private void navigate(String route) {
        UI.getCurrent().navigate(route);
    }
}
