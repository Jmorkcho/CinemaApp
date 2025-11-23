package com.finals.cinema.view;

import com.finals.cinema.configuration.EmailService;
import com.finals.cinema.model.DTO.RegisterDTO;
import com.finals.cinema.model.DTO.UserWithoutPassDTO;
import com.finals.cinema.model.entity.UserStatus;
import com.finals.cinema.model.repository.ConfirmationTokenRepository;
import com.finals.cinema.service.UserService;
import com.finals.cinema.util.exceptions.BadRequestException;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static com.finals.cinema.util.Constants.MAIN_VIEW_ROUTE;


public class RegistrationForm extends FormLayout {

    private H1 title;

    private TextField firstName;
    private TextField lastName;

    private TextField username;

    private EmailField email;

    private PasswordField password;
    private PasswordField passwordConfirm;

    private Button submitButton;

    private Select<String> status;

    private DatePicker datePicker;
    private Locale englishLocale;

    private final Dialog registrationDialog;

    public RegistrationForm(UserService userService, ConfirmationTokenRepository confirmationTokenRepository,
                            //EmailSenderService emailSenderService
                            EmailService emailService, Dialog registrationDialog) {

        this.registrationDialog=registrationDialog;
        getStyle().set("position", "relative");

        title = new H1("Signup form");
        firstName = new TextField("First name");
        lastName = new TextField("Last name");
        username = new TextField("Username");
        email = new EmailField("Email");


        password = new PasswordField("Password");
        passwordConfirm = new PasswordField("Confirm password");

        status = new Select<>();
        status.setLabel("Status");
        status.setItems(UserStatus.getValues());
        status.setValue(UserStatus.getValues().get(0));

        englishLocale = new Locale("en", "EN");

        datePicker = new DatePicker("Date of birth:");
        datePicker.setLocale(englishLocale);

        setRequiredIndicatorVisible(firstName, lastName, email, username, password, passwordConfirm, status, datePicker);

        submitButton = new Button("Register", event ->
        {
            try {

                    UserWithoutPassDTO register = register(userService);
                    sendConfirmationTokenJD(confirmationTokenRepository, register, emailService);
//                    UI.getCurrent().navigate(CONFIRMATION_VIEW_ROUTE);
                    //TODO
                    //popup on register event
                    UI.getCurrent().navigate(MAIN_VIEW_ROUTE);
                    registrationDialog.close();
            } catch (BadRequestException e) {
                Notification.show(e.getMessage(), -1, Notification.Position.BOTTOM_CENTER);
            }
        });
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(title, firstName, lastName, username, email, password, passwordConfirm, status, datePicker, submitButton);

        // Max width of the Form
        setMaxWidth("600px");

        // Allow the form layout to be responsive.
        // On device widths 0-490px we have one column.
        // Otherwise, we have two columns.
        setResponsiveSteps(
                new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("490px", 2, ResponsiveStep.LabelsPosition.TOP));

        // These components always take full width
        setColspan(title, 2);
        setColspan(username, 2);
        setColspan(email, 2);
        setColspan(submitButton, 2);
    }

    private void sendConfirmationTokenJD(ConfirmationTokenRepository confirmationTokenRepository,
                                       UserWithoutPassDTO register, EmailService emailService) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(register.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("kinoarenaproject@gmail.com");

        emailService.sendNewMail(register.getEmail(),"Complete registration","To confirm your account, please click here : " +
                "http://localhost:8888/confirm-account?token=" +
                confirmationTokenRepository.findByUserId(register.getId()).getConfirmationToken());
    }


    private UserWithoutPassDTO register(UserService userService) throws BadRequestException {
        int age = calculateAge(datePicker.getValue());
        RegisterDTO registerDTO = RegisterDTO.builder()
                .firstName(firstName.getValue())
                .lastName(lastName.getValue())
                .username(username.getValue())
                .email(email.getValue())
                .password(password.getValue())
                .confirmPassword(passwordConfirm.getValue())
                .status(status.getValue())
                .age(age)
                .build();

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(registerDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        UserWithoutPassDTO register = userService.registerUser(registerDTO);
        return register;

    }

    private int calculateAge(LocalDate bDay) {
        return Period.between(bDay, LocalDate.now()).getYears();
    }

    public PasswordField getPasswordField() {
        return password;
    }

    public PasswordField getPasswordConfirmField() {
        return passwordConfirm;
    }

    public Button getSubmitButton() {
        return submitButton;
    }


    private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
        Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
    }

    // Constructor for standalone page use -> for RegistrationView.java
    public RegistrationForm(UserService userService, ConfirmationTokenRepository tokenRepo, EmailService emailService) {
        this(userService, tokenRepo, emailService, null); // Call the 4-param constructor with null
    }


}