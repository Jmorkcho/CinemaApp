package com.finals.cinema.view;

import com.finals.cinema.model.entity.Hall;
import com.finals.cinema.model.entity.Projection;
import com.finals.cinema.repository.ProjectionRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.format.DateTimeFormatter;

import static com.finals.cinema.util.Constants.PROJECTION_VIEW_ROUTE;

@Route(value = PROJECTION_VIEW_ROUTE, layout = MainLayout.class)
public class ProjectionView extends VerticalLayout {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    Grid<Projection> grid = new Grid<>(Projection.class, false);
    ProjectionRepository repository;

    public ProjectionView(ProjectionRepository repository) {
        this.repository = repository;
        addClassName("projections-view");
        setSizeFull();
        configureGrid();

        add(getContent());
        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addClassNames("contact-grid");
        grid.addColumn(p -> p.getMovie() == null ? "" : p.getMovie().getTitle())
                .setHeader("Movie").setSortable(true).setFlexGrow(2);
        grid.addColumn(p -> hallCinema(p))
                .setHeader("Cinema").setSortable(true).setFlexGrow(1);
        grid.addColumn(p -> p.getHall() == null ? "" : "Hall " + p.getHall().getNumber())
                .setHeader("Hall").setSortable(true).setFlexGrow(0).setAutoWidth(true);
        grid.addColumn(p -> p.getStartAt() == null ? "" : p.getStartAt().format(DATE_FORMAT))
                .setHeader("Date").setComparator(Projection::getStartAt).setFlexGrow(0).setAutoWidth(true);
        grid.addColumn(p -> p.getStartAt() == null ? "" : p.getStartAt().format(TIME_FORMAT))
                .setHeader("Starts").setFlexGrow(0).setAutoWidth(true);
        grid.addColumn(p -> p.getEndAt() == null ? "" : p.getEndAt().format(TIME_FORMAT))
                .setHeader("Ends").setFlexGrow(0).setAutoWidth(true);
    }

    private static String hallCinema(Projection projection) {
        Hall hall = projection.getHall();
        return hall == null || hall.getCinema() == null ? "" : hall.getCinema().getName();
    }

    private Component getContent() {
        VerticalLayout content = new VerticalLayout(grid);
        content.addClassNames("content");
        content.setSizeFull();
        content.setPadding(false);
        return content;
    }

    private void updateList() {
        grid.setItems(repository.findAll());
    }
}
