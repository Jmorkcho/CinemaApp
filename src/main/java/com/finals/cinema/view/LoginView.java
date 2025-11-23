package com.finals.cinema.view;

import com.finals.cinema.configuration.EmailService;
import com.finals.cinema.model.repository.ConfirmationTokenRepository;
import com.finals.cinema.service.UserService;
import com.finals.cinema.util.exceptions.BadRequestException;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.finals.cinema.util.Constants.*;

@CssImport("./styles/login-view.css")
@Route(value = LOGIN_VIEW_ROUTE)
@PageTitle("Login | Best Cinema")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            // User already logged in - redirect to main view
           event.forwardTo(MAIN_VIEW_ROUTE);
           return;
        }
    }

    //LoginForm login = new LoginForm();

    public LoginView(UserService userService, ConfirmationTokenRepository confirmationTokenRepository, EmailService emailService) {
        addClassName("login-view");
        UI ui = UI.getCurrent();
        ui.getPage().executeJs(
                "const dark = localStorage.getItem('darkTheme') === 'true';" +
                        "if (dark) {" +
                        "  document.documentElement.setAttribute('theme', 'dark');" +
                        "}", ui);

        getElement().getClassList().add("v-visible");
        System.out.println("LoginView loaded");

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "869fcb");
       //login.setAction("login");
        H1 title = new H1("Welcome to Best Cinema");
        var username = new TextField("Username");
        var password = new PasswordField("Password");
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
                Notification.show(e.getMessage(), 2500, Notification.Position.BOTTOM_CENTER);
            }
        });
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClickShortcut(Key.ENTER);
        Button openRegistrationButton = new Button("RegisterDialog", event -> openRegistrationDialog(userService, confirmationTokenRepository, emailService));
        add(toggleButtonTheme, title, username, password, loginButton, registrationButton, openRegistrationButton);

    }

    private void login(UserService userService, TextField username, PasswordField password) throws BadRequestException {
        userService.logInUser(username.getValue(), password.getValue());
        UI.getCurrent().navigate(MAIN_VIEW_ROUTE);
    }


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

    Button registrationButton = new Button("Register", event -> UI.getCurrent().navigate(REGISTRATION_VIEW_ROUTE));

    private void openRegistrationDialog(UserService userService, ConfirmationTokenRepository confirmationTokenRepository, EmailService emailService) {
        Dialog registrationDialog = new Dialog();
        registrationDialog.setCloseOnEsc(true);
        registrationDialog.setCloseOnOutsideClick(true);

        RegistrationForm registrationForm = new RegistrationForm(userService, confirmationTokenRepository, emailService, registrationDialog);
        registrationDialog.add(registrationForm);

        // Optionally, you can add a close button
        Button closeButton = new Button("Close", event -> registrationDialog.close());
        registrationDialog.add(closeButton);

        registrationDialog.open();
    }

    @ClientCallable
    public void applyDarkTheme() {
        UI.getCurrent().getElement().getThemeList().add(Lumo.DARK);
    }
}