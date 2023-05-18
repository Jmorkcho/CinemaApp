package com.finals.cinema.model.views.list;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(value = "login")
@PageTitle("Login | University Project")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "869fcb");
        Button login1 = new Button("Login");
        login.setAction("login");

        //////////////
        //HorizontalLayout back = new HorizontalLayout();
        //back.setSizeFull();
        //back.getStyle().set("background","red");
        //////////////
        add(toggleButtonTheme, new H1("University Project"), login, registrationButton);
    }

    Button toggleButtonTheme = new Button("Toggle dark theme", click -> {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList(); // (1)

        if (themeList.contains(Lumo.DARK)) { // (2)
            themeList.remove(Lumo.DARK);
        } else {
            themeList.add(Lumo.DARK);
        }
    });

    Button registrationButton = new Button("Register", event -> UI.getCurrent().navigate("registration"));

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // inform the user about an authentication error
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }

    }
}