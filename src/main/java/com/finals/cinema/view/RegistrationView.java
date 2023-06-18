package com.finals.cinema.view;

import com.finals.cinema.configuration.EmailService;
import com.finals.cinema.model.repository.ConfirmationTokenRepository;
import com.finals.cinema.service.UserService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


import static com.finals.cinema.util.Constants.REGISTRATION_VIEW_ROUTE;

@Route(value = REGISTRATION_VIEW_ROUTE)
@AnonymousAllowed
@ComponentScan(basePackages = { "com.finals.cinema" })
@EnableAutoConfiguration
public class RegistrationView extends VerticalLayout {

    public RegistrationView(UserService userService, ConfirmationTokenRepository confirmationTokenRepository,
                            //EmailSenderService emailSenderService
                            EmailService emailService) {


        RegistrationForm registrationForm = new RegistrationForm(userService, confirmationTokenRepository,
                //emailSenderService
                emailService);
        // Center the RegistrationForm
        setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);
        add(registrationForm);
    }
}