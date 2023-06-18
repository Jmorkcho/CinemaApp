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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;
import java.util.stream.Stream;

import static com.finals.cinema.util.Constants.MAIN_VIEW_ROUTE;


public class RegistrationForm extends FormLayout {

    private H3 title;

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

    public RegistrationForm(UserService userService, ConfirmationTokenRepository confirmationTokenRepository,
                            //EmailSenderService emailSenderService
                            EmailService emailService) {

        title = new H3("Signup form");
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
                if (!firstName.getValue().isBlank() && !lastName.getValue().isBlank() &&
                        !username.getValue().isBlank() && !email.getValue().isBlank() && !password.getValue().isBlank() &&
                        !passwordConfirm.getValue().isBlank() && !status.getValue().isBlank() && datePicker.getValue() != null) {
                    UserWithoutPassDTO register = register(userService);
                    sendConfirmationTokenJD(confirmationTokenRepository, register, emailService);
//                    UI.getCurrent().navigate(CONFIRMATION_VIEW_ROUTE);
                    UI.getCurrent().navigate(MAIN_VIEW_ROUTE);
                } else {
                    throw new BadRequestException("Please fill all the necessary fields");
                }
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

   // private void sendConfirmationToken(ConfirmationTokenRepository confirmationTokenRepository,
   //                                    UserWithoutPassDTO register, EmailSenderService emailSenderService) {
//
   //     SimpleMailMessage mailMessage = new SimpleMailMessage();
   //     mailMessage.setTo(register.getEmail());
   //     mailMessage.setSubject("Complete Registration!");
   //     mailMessage.setFrom("kinoarenaproject@gmail.com");
   //     mailMessage.setText("To confirm your account, please click here : " +
   //             "http://localhost:8888/confirm-account?token=" +
   //             confirmationTokenRepository.findByUserId(register.getId()).getConfirmationToken());
   //     emailSenderService.sendEmail(mailMessage);
   // }

    private void sendConfirmationTokenJD(ConfirmationTokenRepository confirmationTokenRepository,
                                       UserWithoutPassDTO register, EmailService emailService) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(register.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("kinoarenaproject@gmail.com");
        //mailMessage.setText("To confirm your account, please click here : " +
        //        "http://localhost:8888/confirm-account?token=" +
        //        confirmationTokenRepository.findByUserId(register.getId()).getConfirmationToken());

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

}