package com.finals.cinema.view;


import com.finals.cinema.model.DTO.AddMovieDTO;
import com.finals.cinema.service.MovieService;
import com.finals.cinema.service.UserService;
import com.finals.cinema.util.exceptions.UnauthorizedException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;



public class MainLayout extends AppLayout {

    TextField tittle;
    NumberField ageRes;
    NumberField genreId;

    public MainLayout(UserService userService, MovieService movieService) {
        createHeader(userService, movieService);
//        createDrawer();
    }

    private void createHeader(UserService userService, MovieService movieService) {
        H1 logo = new H1("Best Cinema");
        logo.addClassNames("text-l", "m-m");

        Button logout = new Button("Log out", e -> userService.logout());

        tittle = new TextField("title");
        ageRes = new NumberField("age");
        genreId = new NumberField("genre");


        Button addMovie = new Button("Add Movie", e -> {
            AddMovieDTO dto = AddMovieDTO.builder().title(tittle.getValue())
                    .ageRestriction(ageRes.getValue().intValue())
                    .genreId(genreId.getValue().intValue()).build();
            try {
                movieService.addMovie(dto, 2);
            } catch (Exception ex) {
                ex.printStackTrace();
            } catch (UnauthorizedException ex) {
                ex.printStackTrace();
            }
        });

        Button toggleButtonTheme = new Button("Toggle dark theme", click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList(); // (1)

            if (themeList.contains(Lumo.DARK)) { // (2)
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                logo,tittle, ageRes, genreId, addMovie, toggleButtonTheme, logout
        );

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);

    }

    private void createDrawer() {
        RouterLink listLink = new RouterLink("Newest Films", CinemaView.class);
        listLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
                listLink,
                new RouterLink("Buy Tickets", TicketView.class),
                new RouterLink("Cinemas", CinemaView.class),
                new RouterLink("Projections", ProjectionView.class)
        ));
    }
}