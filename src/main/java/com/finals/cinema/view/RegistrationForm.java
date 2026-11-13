package com.finals.cinema.view;

import com.finals.cinema.service.EmailService;
import com.finals.cinema.model.DTO.RegisterDTO;
import com.finals.cinema.model.DTO.UserWithoutPassDTO;
import com.finals.cinema.model.entity.UserStatus;
import com.finals.cinema.repository.ConfirmationTokenRepository;
import com.finals.cinema.service.UserService;
import com.finals.cinema.util.exceptions.NotFoundException;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.html.Div;
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

    private final Div formError = new Div();

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

        englishLocale = Locale.of("en", "EN");

        datePicker = new DatePicker("Date of birth:");
        datePicker.setLocale(englishLocale);

        setRequiredIndicatorVisible(firstName, lastName, email, username, password, passwordConfirm, status, datePicker);

        submitButton = new Button("Register", event ->
        {
            clearErrors();
            try {
                    if (datePicker.getValue() == null) {
                        datePicker.setErrorMessage("Please select your date of birth");
                        datePicker.setInvalid(true);
                        return;
                    }
                    if (!password.getValue().equals(passwordConfirm.getValue())) {
                        passwordConfirm.setErrorMessage("Passwords must match");
                        passwordConfirm.setInvalid(true);
                        return;
                    }
                    UserWithoutPassDTO register = register(userService);
                    sendConfirmationTokenJD(confirmationTokenRepository, register, emailService);
//                    UI.getCurrent().navigate(CONFIRMATION_VIEW_ROUTE);
                    //TODO
                    //popup on register event
                    UI.getCurrent().navigate(MAIN_VIEW_ROUTE);
                    registrationDialog.close();
            } catch (ConstraintViolationException e) {
                showViolations(e.getConstraintViolations());
            } catch (Exception e) {
                formError.setText(e.getMessage() == null ? "Registration failed" : e.getMessage());
                formError.setVisible(true);
            }
        });
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        formError.getStyle().set("color", "var(--lumo-error-text-color)");
        formError.setVisible(false);

        firstName.addValueChangeListener(e -> firstName.setInvalid(false));
        lastName.addValueChangeListener(e -> lastName.setInvalid(false));
        username.addValueChangeListener(e -> username.setInvalid(false));
        email.addValueChangeListener(e -> email.setInvalid(false));
        password.addValueChangeListener(e -> password.setInvalid(false));
        passwordConfirm.addValueChangeListener(e -> passwordConfirm.setInvalid(false));
        datePicker.addValueChangeListener(e -> datePicker.setInvalid(false));

        add(title, firstName, lastName, username, email, password, passwordConfirm, status, datePicker, formError, submitButton);
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
        setColspan(formError, 2);
        setColspan(submitButton, 2);
    }

    private void clearErrors() {
        for (HasValidation field : new HasValidation[]{firstName, lastName, username, email, password, passwordConfirm, datePicker}) {
            field.setInvalid(false);
        }
        formError.setVisible(false);
    }

    private void showViolations(Set<ConstraintViolation<?>> violations) {
        for (ConstraintViolation<?> violation : violations) {
            String property = violation.getPropertyPath().toString();
            HasValidation target = switch (property) {
                case "firstName" -> firstName;
                case "lastName" -> lastName;
                case "username" -> username;
                case "email" -> email;
                case "password" -> password;
                case "confirmPassword" -> passwordConfirm;
                case "age" -> datePicker;
                default -> null;
            };
            if (target == null) {
                formError.setText(violation.getMessage());
                formError.setVisible(true);
            } else if (!target.isInvalid()) {
                target.setErrorMessage(violation.getMessage());
                target.setInvalid(true);
            }
        }
    }

    private void sendConfirmationTokenJD(ConfirmationTokenRepository confirmationTokenRepository,
                                       UserWithoutPassDTO register, EmailService emailService) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(register.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("kinoarenaproject@gmail.com");

        String confirmationToken = confirmationTokenRepository.findByUserId(register.getId())
          .orElseThrow(() -> new NotFoundException("Confirmation token not found"))
          .getConfirmationToken();

        emailService.sendNewMail(register.getEmail(),
          "Complete registration",
          "To confirm your account, please click here : " + "http://localhost:8888/confirm-account?token=" + confirmationToken);
    }


    private UserWithoutPassDTO register(UserService userService) {
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

        // todo make abstract
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