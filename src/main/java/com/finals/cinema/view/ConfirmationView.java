package com.finals.cinema.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import static com.finals.cinema.util.Constants.CONFIRMATION_VIEW_ROUTE;
import static com.finals.cinema.util.Constants.LOGIN_VIEW_ROUTE;

@Route(value = CONFIRMATION_VIEW_ROUTE)
public class ConfirmationView extends VerticalLayout implements HasUrlParameter<String> {

    LoginForm login = new LoginForm();
    String email;

    public ConfirmationView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "869fcb");
        login.setAction("login");
        H1 title = new H1("A confirmation email was sent to " + email);

        Button loginButton = new Button("Return to Login page", event ->
        {
            UI.getCurrent().navigate(LOGIN_VIEW_ROUTE);
        });
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(title,loginButton);

    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        email = s;
    }
}
