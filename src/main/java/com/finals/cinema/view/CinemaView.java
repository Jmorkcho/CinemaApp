package com.finals.cinema.view;

import com.finals.cinema.model.entity.Cinema;
import com.finals.cinema.model.repository.CinemaRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;


import static com.finals.cinema.util.Constants.CINEMA_VIEW_ROUTE;

@Route(value = CINEMA_VIEW_ROUTE, layout = MainLayout.class)
@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
public class CinemaView extends VerticalLayout {

    Grid<Cinema> grid = new Grid<>(Cinema.class, false);
    CinemaRepository repository;

    public CinemaView(CinemaRepository repository) {
        this.repository = repository;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        add(getContent());
        updateList();
    }

    private void configureGrid() {
        grid.setWidth("400px");
        grid.addClassNames("contact-grid");
        grid.addColumn(Cinema::getName).setHeader("Name").setFlexGrow(0).setWidth("199px");
        grid.addColumn(Cinema::getCity).setHeader("City").setFlexGrow(0).setWidth("199px");
    }

    private Component getContent() {
        Image image = new Image("images/ideaForProjLayout.png","");
        VerticalLayout content = new VerticalLayout(image);
        content.setFlexGrow(0, grid);
        content.addClassNames("content");
        content.setSizeFull();
        content.setWidth("400px");
        return content;
    }

    private void updateList() {
        grid.setItems(repository.findAll());
    }
}

