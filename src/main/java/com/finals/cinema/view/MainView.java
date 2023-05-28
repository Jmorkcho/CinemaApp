package com.finals.cinema.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static com.finals.cinema.util.Constants.MAIN_VIEW_ROUTE;

@Route(value = MAIN_VIEW_ROUTE, layout = MainLayout.class)
@PageTitle("Main")
//TODO
//@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
public class MainView extends VerticalLayout {

    public MainView() {
        addClassName("status-list-view");
        setSizeFull();
    }
}

