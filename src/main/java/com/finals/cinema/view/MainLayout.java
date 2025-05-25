package com.finals.cinema.view;


import com.finals.cinema.service.MovieService;
import com.finals.cinema.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;

import static com.finals.cinema.util.Constants.*;


public class MainLayout extends AppLayout {


    public MainLayout(UserService userService, MovieService movieService) {
        createHeader(userService, movieService);
        createDrawer();
    }

    public void createHeader(UserService userService, MovieService movieService) {
        H1 logo = new H1("Best Cinema");
        Button LogoMain = new Button("Best Cinema", event -> {
            UI.getCurrent().navigate(MAIN_VIEW_ROUTE);
        });
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
                LogoMain,
                new Div() // This acts as a spacer
        );

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

// Create a new layout for the right-aligned buttons
        HorizontalLayout rightButtons = new HorizontalLayout(whatIsOn, allCinemas, toggleButtonTheme, logout);
        rightButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        rightButtons.setWidth("100%");

// Add the right buttons layout to the header
        header.add(rightButtons);

        addToNavbar(header);

    }

    private void getAllCinemas() {

    }

    private void getWhatIsOn() {

    }

    private void createDrawer() {
        RouterLink listLink = new RouterLink("Newest Films", CinemaView.class);
        listLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(


                new RouterLink("Buy Tickets", TicketView.class),
                new RouterLink("Cinemas", CinemaView.class),
                new RouterLink("Projections", ProjectionView.class)
        ));
    }
}