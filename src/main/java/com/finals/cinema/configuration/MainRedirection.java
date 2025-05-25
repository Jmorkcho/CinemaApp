package com.finals.cinema.configuration;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("/") // This defines the root route
public class MainRedirection extends VerticalLayout {

    public MainRedirection() {
        // Redirect to the MainView
        UI.getCurrent().navigate("main");
    }
}