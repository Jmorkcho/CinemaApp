package com.finals.cinema.configuration;

import com.finals.cinema.view.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;


import static com.finals.cinema.util.Constants.MAIN_VIEW_ROUTE;

@Route("")
@PageTitle("Redirecting")
public class RootRedirectView extends Div implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
       event.forwardTo(MAIN_VIEW_ROUTE);
    }
}