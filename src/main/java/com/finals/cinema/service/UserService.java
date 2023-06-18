package com.finals.cinema.service;

import com.finals.cinema.model.entity.UserStatus;
import com.finals.cinema.util.Constants;
import com.finals.cinema.util.exceptions.BadRequestException;
import com.finals.cinema.model.DTO.RegisterDTO;
import com.finals.cinema.model.DTO.EditUserPasswordDTO;
import com.finals.cinema.model.DTO.UserWithoutPassDTO;
import com.finals.cinema.model.DTO.UserWithoutTicketAndPassDTO;
import com.finals.cinema.model.entity.ConfirmationToken;
import com.finals.cinema.model.entity.User;
import com.finals.cinema.model.repository.ConfirmationTokenRepository;
import com.finals.cinema.view.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.finals.cinema.util.Constants.*;

@Service
public class UserService extends AbstractService {

    public record AuthorizedRoute(String route, String name, Class<? extends Component> view) { }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Transactional
    public UserWithoutPassDTO registerUser(RegisterDTO registerDTO) throws BadRequestException {
        if (registerDTO != null) {
            if (userRepository.findByEmail(registerDTO.getEmail()) != null) {
                throw new BadRequestException("There is already a user with that email address: " + registerDTO.getEmail());
            }
            if (userRepository.findByUsername(registerDTO.getUsername()) != null) {
                throw new BadRequestException("There is already a user with that username: " + registerDTO.getUsername());
            }
            if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
                throw new BadRequestException("Passwords must match");
            }
            User user = User.builder()
                    .username(registerDTO.getUsername())
                    .password(passwordEncoder.encode(registerDTO.getPassword()))
                    .email(registerDTO.getEmail())
                    .firstName(registerDTO.getFirstName())
                    .lastName(registerDTO.getLastName())
                    .age(registerDTO.getAge())
                    .roleId(Constants.ROLE_USER)
                    .statusId(UserStatus.valueOf(registerDTO.getStatus().toUpperCase()).ordinal() + 1)
                    .createdAt(LocalDateTime.now())
                    .tickets(new ArrayList<>())
                    .isEnabled(false)
                    .build();
            user = userRepository.save(user);
            ConfirmationToken confirmationToken = new ConfirmationToken(user);
            confirmationTokenRepository.save(confirmationToken);
            return new UserWithoutPassDTO(user);
        }
        throw new BadRequestException("Fill in missing fields");
    }


    public UserWithoutTicketAndPassDTO logInUser(String username, String password) throws BadRequestException {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {

            VaadinSession.getCurrent().setAttribute(User.class, user);
            createRoutes(user.getRoleId());
            return new UserWithoutTicketAndPassDTO(user);
        }
        throw new BadRequestException("Username or Password incorrect");
    }

    public UserWithoutTicketAndPassDTO changePassword(EditUserPasswordDTO passwordDTO, int userId) throws BadRequestException {
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new BadRequestException("Passwords must match");
        }
        User user = userRepository.findById(userId).get();
        if (passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            return new UserWithoutTicketAndPassDTO(userRepository.save(user));
        }
        throw new BadRequestException("Username or Password incorrect");
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(LOGIN_VIEW_ROUTE);
        VaadinSession.getCurrent().getSession().invalidate();
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                null);
    }

    private void createRoutes(int role) {
        getAuthorizedRoutes(role).stream()
                .forEach(route ->
                        RouteConfiguration.forSessionScope().setRoute(
                                route.route, route.view, MainLayout.class));

    }


    public List<AuthorizedRoute> getAuthorizedRoutes(int role) {
        var routes = new ArrayList<AuthorizedRoute>();
        if (role == ROLE_USER) {
            routes.add(new AuthorizedRoute("", "Login", LoginView.class));
            routes.add(new AuthorizedRoute("main", "Home", MainView.class));
            routes.add(new AuthorizedRoute("tickets", "Home", TicketView.class));
            routes.add(new AuthorizedRoute("projections", "Logout", ProjectionView.class));
            routes.add(new AuthorizedRoute("cinemas", "Logout", CinemaView.class));
        } else if (role == ROLE_ADMIN) {
            routes.add(new AuthorizedRoute("", "Login", LoginView.class));
            routes.add(new AuthorizedRoute("main", "Home", MainView.class));
            routes.add(new AuthorizedRoute("tickets", "Home", TicketView.class));
            routes.add(new AuthorizedRoute("projections", "Logout", ProjectionView.class));
            routes.add(new AuthorizedRoute("cinemas", "Logout", CinemaView.class));
            routes.add(new AuthorizedRoute("admin_panel", "Admin Panel", AdminView.class));
        }
        return routes;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
