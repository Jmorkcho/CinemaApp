package com.finals.cinema.view;

import com.finals.cinema.model.repository.ConfirmationTokenRepository;
import com.finals.cinema.service.EmailSenderService;
import com.finals.cinema.service.UserService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import static com.finals.cinema.util.Constants.REGISTRATION_VIEW_ROUTE;

@Route(value = REGISTRATION_VIEW_ROUTE)
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {

    public RegistrationView(UserService userService, ConfirmationTokenRepository confirmationTokenRepository,
                            EmailSenderService emailSenderService) {

        RegistrationForm registrationForm = new RegistrationForm(userService, confirmationTokenRepository, emailSenderService);
        // Center the RegistrationForm
        setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);
        add(registrationForm);
    }



}