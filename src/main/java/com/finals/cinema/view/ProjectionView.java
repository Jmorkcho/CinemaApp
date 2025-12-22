package com.finals.cinema.view;

import com.finals.cinema.model.entity.Projection;
import com.finals.cinema.repository.ProjectionRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import static com.finals.cinema.util.Constants.PROJECTION_VIEW_ROUTE;

@Route(value = PROJECTION_VIEW_ROUTE, layout = MainLayout.class)
public class ProjectionView extends VerticalLayout {

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
        grid.setWidth("400px");

        grid.addClassNames("contact-grid");
        grid.addColumn(Projection::getMovie).setHeader("Name").setFlexGrow(0).setWidth("199px");
        grid.addColumn(Projection::getHall).setHeader("Hall").setFlexGrow(0).setWidth("199px");

    }

    private Component getContent() {
        //Image image = new Image("images/ideaForProjLayout.png","");
        VerticalLayout content = new VerticalLayout(grid);
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
