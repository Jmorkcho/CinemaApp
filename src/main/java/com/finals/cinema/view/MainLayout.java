package com.finals.cinema.view;


import com.finals.cinema.configuration.EmailService;
import com.finals.cinema.model.repository.ConfirmationTokenRepository;
import com.finals.cinema.service.MovieService;
import com.finals.cinema.service.UserService;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.finals.cinema.util.Constants.*;


public class MainLayout extends AppLayout {


    public MainLayout(UserService userService, MovieService movieService,
                      ConfirmationTokenRepository confirmationTokenRepository, EmailService emailService) {
        createHeader(userService, movieService, confirmationTokenRepository, emailService);
        createDrawer();
        UI ui = UI.getCurrent();
        ui.getPage().executeJs(
                "const dark = localStorage.getItem('darkTheme') === 'true';" +
                        "if (dark) {" +
                        "  document.documentElement.setAttribute('theme', 'dark');" +
                        "  $0.$server.applyDarkTheme();" +
                        "}", ui);
    }

    public void createHeader(UserService userService, MovieService movieService,
                             ConfirmationTokenRepository confirmationTokenRepository, EmailService emailService) {
        H1 logo = new H1("Best Cinema");
        Button LogoMain = new Button("Best Cinema", event -> {
            UI.getCurrent().navigate(MAIN_VIEW_ROUTE);
        });
        logo.addClassNames("text-l", "m-m");

        Button logout = new Button("Log out", event -> userService.logout());

        Button toggleButtonTheme = new Button("\uD83C\uDF17", click -> {
            UI.getCurrent().getPage().executeJs(
                    "const html = document.documentElement;" +
                            "const isDark = html.getAttribute('theme') === 'dark';" +
                            "if (isDark) {" +
                            "  html.removeAttribute('theme');" +
                            "  localStorage.setItem('darkTheme', 'false');" +
                            "} else {" +
                            "  html.setAttribute('theme', 'dark');" +
                            "  localStorage.setItem('darkTheme', 'true');" +
                            "}"
            );
        });

        Button login = new Button("Login", event -> openLoginDialog(userService, confirmationTokenRepository, emailService));

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                LogoMain,
                new Div() // This acts as a spacer
        );

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            // User is logged in, show logout button
            HorizontalLayout rightButtons = new HorizontalLayout(toggleButtonTheme, logout);
            rightButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            rightButtons.setWidth("100%");

            header.add(rightButtons);

            addToNavbar(header);
            System.out.println("User is authenticated.");
        } else
        {
            // User is not logged in, don't show out logout button
            HorizontalLayout rightButtons = new HorizontalLayout(toggleButtonTheme, login);
            rightButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            rightButtons.setWidth("100%");

            header.add(rightButtons);

            addToNavbar(header);
            System.out.println("User is not authenticated.");
        }

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
//                new RouterLink("WhatIsOn", WhatIsOn.class)
        ));
    }

    private void openLoginDialog(UserService userService, ConfirmationTokenRepository confirmationTokenRepository, EmailService emailService) {
        Dialog loginDialog = new Dialog();
        loginDialog.setCloseOnEsc(true);
        loginDialog.setCloseOnOutsideClick(true);

        LoginForm loginForm = new LoginForm(userService, confirmationTokenRepository, emailService, loginDialog);

        Button closeButton = new Button("✖", event -> loginDialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getStyle()
                .set("margin-left", "auto")
                .set("margin-top", "0")
                .set("margin-right", "0")
                .set("position", "absolute")
                .set("top", "0.5rem")
                .set("right", "0.5rem");

        loginDialog.add(closeButton, loginForm);
        loginDialog.open();
    }

}