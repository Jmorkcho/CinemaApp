package com.finals.cinema.view;


import com.finals.cinema.service.UserService;
import com.finals.cinema.util.exceptions.BadRequestException;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import static com.finals.cinema.util.Constants.*;

@Route(value = LOGIN_VIEW_ROUTE)
@PageTitle("Login | Best Cinema")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    LoginForm login = new LoginForm();
    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");

    public LoginView(UserService userService) {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "869fcb");
        login.setAction("login");
        H1 title = new H1("Welcome to Best Cinema");

        Button loginButton = new Button("Login", event ->
        {
            try {
                if (!username.getValue().isBlank() && !password.getValue().isBlank())
                {
                    login(userService, username, password);
                }
                else
                {
                    throw new BadRequestException("Please fill all the necessary fields");
                }
            } catch (BadRequestException e) {
                Notification.show(e.getMessage(), -1, Notification.Position.BOTTOM_CENTER);
            }
        });
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClickShortcut(Key.ENTER);

        add(toggleButtonTheme, title, username, password, loginButton, registrationButton);

    }

    @Bean
    public UserDetailsManager userDetailsService() {
        UserDetails user =
                User.withUsername(username.toString())
                        .password(password.toString())
                        .roles("ROLE_USER")
                        .build();
//        UserDetails admin =
//                User.withUsername("admin")
//                        .password("{noop}admin")
//                        .roles("ADMIN")
//                        .build();
        return new InMemoryUserDetailsManager(user
                //, admin
                );
    }

    private void login(UserService userService, TextField username, PasswordField password) throws BadRequestException {
        userService.logInUser(username.getValue(), password.getValue());
        UI.getCurrent().navigate(MAIN_VIEW_ROUTE);
    }


    Button toggleButtonTheme = new Button("Toggle dark theme", click -> {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList(); // (1)

        if (themeList.contains(Lumo.DARK)) { // (2)
            themeList.remove(Lumo.DARK);
        } else {
            themeList.add(Lumo.DARK);
        }
    });

    Button registrationButton = new Button("Register", event -> UI.getCurrent().navigate(REGISTRATION_VIEW_ROUTE));
}