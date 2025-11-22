package com.finals.cinema.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("movie-details/:id")
public class MovieDetailsView extends VerticalLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get("id").orElse("null");
        System.out.println("DEBUG: setParameter called with id = " + id);
        removeAll();
        if (id != null) {
            add(new H1("Movie ID: " + id));
        } else {
            add(new H1("No movie ID provided."));
        }
    }
}