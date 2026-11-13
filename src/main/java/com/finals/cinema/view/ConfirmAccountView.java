package com.finals.cinema.view;

import com.finals.cinema.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

import static com.finals.cinema.util.Constants.MAIN_VIEW_ROUTE;

/**
 * Target of the link in the registration email: /confirm-account?token=...
 */
@Route("confirm-account")
@PageTitle("Confirm account")
public class ConfirmAccountView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;

    public ConfirmAccountView(UserService userService) {
        this.userService = userService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();
        List<String> tokens = event.getLocation().getQueryParameters().getParameters().get("token");
        String message;
        boolean success = false;
        try {
            if (tokens == null || tokens.isEmpty()) {
                throw new IllegalArgumentException("The confirmation link is missing its token");
            }
            userService.confirmAccount(tokens.get(0));
            message = "Your account has been confirmed. You can now log in.";
            success = true;
        } catch (Exception e) {
            message = e.getMessage() == null ? "The confirmation link is not valid" : e.getMessage();
        }

        H1 title = new H1(success ? "Account confirmed" : "Confirmation failed");
        Button toMain = new Button("Go to the main page", e -> UI.getCurrent().navigate(MAIN_VIEW_ROUTE));
        toMain.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(title, new Paragraph(message), toMain);
    }
}
