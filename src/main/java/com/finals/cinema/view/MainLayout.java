package com.finals.cinema.view;


import com.finals.cinema.service.MovieService;
import com.finals.cinema.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.theme.lumo.Lumo;

import static com.finals.cinema.util.Constants.CINEMA_VIEW_ROUTE;


public class MainLayout extends AppLayout {

    public MainLayout(UserService userService, MovieService movieService) {
        createHeader(userService, movieService);
//        createDrawer();
    }

    private void createHeader(UserService userService, MovieService movieService) {
        H1 logo = new H1("Best Cinema");
        logo.addClassNames("text-l", "m-m");

        Button logout = new Button("Log out", event -> userService.logout());

        Button toggleButtonTheme = new Button("Toggle dark theme", click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList(); // (1)

            if (themeList.contains(Lumo.DARK)) { // (2)
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });

        Button whatIsOn = new Button("What's on", event -> getWhatIsOn());

        Button allCinemas = new Button("Cinemas", event -> {
            UI.getCurrent().navigate(CINEMA_VIEW_ROUTE);
        });

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                logo, whatIsOn, allCinemas, toggleButtonTheme, logout
        );

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);

    }

    private void getAllCinemas() {

    }

    private void getWhatIsOn() {

    }

    private void createDrawer() {
        //RouterLink listLink = new RouterLink("Newest Films", CinemaView.class);
        //listLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(

                //listLink
                //new RouterLink("Buy Tickets", TicketView.class),
                //new RouterLink("Cinemas", CinemaView.class),
                //new RouterLink("Projections", ProjectionView.class)
        ));
    }
}