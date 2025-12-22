package com.finals.cinema.view;

import com.finals.cinema.service.EmailService;
import com.finals.cinema.repository.ConfirmationTokenRepository;
import com.finals.cinema.service.UserService;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.Lumo;

import static com.finals.cinema.util.Constants.*;

public class LoginForm extends FormLayout{


    private final Dialog loginDialog;

    public LoginForm(UserService userService, ConfirmationTokenRepository confirmationTokenRepository,
                     EmailService emailService, Dialog loginDialog)  {
            this.loginDialog = loginDialog;

            setWidth("300px");
            getStyle().set("margin", "auto");
            H1 title = new H1("Login");
            var username = new TextField("Username");
            username.setPlaceholder("Type your username...");
            var password = new PasswordField("Password");
            password.setPlaceholder("Type your password...");
            Button loginButton = new Button("Login", event ->
            {
                try {
                    if (!username.getValue().isBlank() && !password.getValue().isBlank())
                    {
                        login(userService, username, password);
                    }
                    else
                    {
                        throw new IllegalArgumentException("Please fill all the necessary fields");
                    }
                } catch (Exception e) {
                    Notification.show(e.getMessage(), 2500, Notification.Position.BOTTOM_CENTER);
                }
            });
            loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            loginButton.addClickShortcut(Key.ENTER);
            Button openRegistrationButton = new Button("Register", event -> openRegistrationDialog(userService, confirmationTokenRepository, emailService));
            add(title, username, password, loginButton, openRegistrationButton);

            FormLayout formLayout = this;
            this.setWidth("50%");
            formLayout.setColspan(title, 1);
            formLayout.setColspan(username, 1);
            formLayout.setColspan(password, 1);
            formLayout.setColspan(loginButton, 1);
            formLayout.setColspan(openRegistrationButton, 1);
        }

        private void login(UserService userService, TextField username, PasswordField password) {
            userService.logIn(username.getValue(), password.getValue());
            loginDialog.close();
            //UI.getCurrent().getPage().reload();
            UI.getCurrent().navigate("dummy");
            UI.getCurrent().navigate(MAIN_VIEW_ROUTE);
        }


        private void openRegistrationDialog(UserService userService, ConfirmationTokenRepository confirmationTokenRepository, EmailService emailService) {
            Dialog registrationDialog = new Dialog();
            registrationDialog.setCloseOnEsc(true);
            registrationDialog.setCloseOnOutsideClick(true);

            Button closeButton = new Button("✖", event -> registrationDialog.close());
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            closeButton.getStyle()
                    .set("position", "absolute")
                    .set("top", "0.5rem")
                    .set("right", "0.5rem")
                    .set("width", "21.9667px")
                    .set("height", "26px");

            RegistrationForm registrationForm = new RegistrationForm(userService, confirmationTokenRepository, emailService, registrationDialog);
            registrationDialog.add(closeButton, registrationForm);

            registrationDialog.open();
        }

        @ClientCallable
        public void applyDarkTheme() {
            UI.getCurrent().getElement().getThemeList().add(Lumo.DARK);
        }
    }
