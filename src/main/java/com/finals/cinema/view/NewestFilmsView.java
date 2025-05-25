//package com.finals.cinema.view;
//
//import com.finals.cinema.model.entity.Cinema;
//import com.finals.cinema.model.entity.NewestFilm;
//import com.finals.cinema.model.repository.NewestFilmsRepository;
//import com.vaadin.flow.component.Component;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.router.Route;
//
//import static com.finals.cinema.util.Constants.NEWEST_FILMS_VIEW_ROUTE;
//
//@Route(value = NEWEST_FILMS_VIEW_ROUTE, layout = MainLayout.class)
//public class NewestFilms extends VerticalLayout {
//
//    Grid<NewestFilms> grid = new Grid<>(NewestFilms.class, false);
//    NewestFilmsRepository repository;
//
//    public NewestFilms(NewestFilmsRepository repository) {
//        this.repository = repository;
//        addClassName("list-view");
//        setSizeFull();
//        configureGrid();
//
//        add(getContent());
//        updateList();
//    }
//
//    private void configureGrid() {
//        grid.setWidth("400px");
//
//        grid.addClassNames("contact-grid");
//        grid.addColumn(NewestFilm::getTitle).setHeader("Name").setFlexGrow(0).setWidth("199px");
//        grid.addColumn(Cinema::getCity).setHeader("City").setFlexGrow(0).setWidth("199px");
//
//    }
//
//    private Component getContent() {
//        //Image image = new Image("images/ideaForProjLayout.png","");
//        VerticalLayout content = new VerticalLayout(grid);
//        content.setFlexGrow(0, grid);
//        content.addClassNames("content");
//        content.setSizeFull();
//        content.setWidth("400px");
//        return content;
//    }
//
//    private void updateList() {
//        grid.setItems(repository.findAll());
//    }
//}
//
