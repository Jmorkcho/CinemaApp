package com.finals.cinema.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@Route("x123abc/:id")
public class MovieDetailsView extends VerticalLayout implements HasUrlParameter<Integer> {

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer id) {
        System.out.println("DEBUG: setParameter called with id = " + id);
        removeAll();
        if (id != null) {
            add(new H1("Movie ID: " + id));
        } else {
            add(new H1("No movie ID provided."));
        }
    }
}