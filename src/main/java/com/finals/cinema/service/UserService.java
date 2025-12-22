package com.finals.cinema.service;

import com.finals.cinema.model.entity.UserRole;
import com.finals.cinema.model.entity.UserStatus;
import com.finals.cinema.repository.UserRepository;
import com.finals.cinema.model.DTO.RegisterDTO;
import com.finals.cinema.model.DTO.EditUserPasswordDTO;
import com.finals.cinema.model.DTO.UserWithoutPassDTO;
import com.finals.cinema.model.DTO.UserWithoutTicketAndPassDTO;
import com.finals.cinema.model.entity.ConfirmationToken;
import com.finals.cinema.model.entity.User;
import com.finals.cinema.repository.ConfirmationTokenRepository;
import com.finals.cinema.util.exceptions.NotFoundException;
import com.finals.cinema.util.exceptions.UnauthorizedException;
import com.finals.cinema.view.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.finals.cinema.util.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public record AuthorizedRoute(String route, String name, Class<? extends Component> view) { }

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Transactional
    public UserWithoutPassDTO registerUser(RegisterDTO registerDTO) {
        if (registerDTO != null) {
            throw new IllegalArgumentException("Register form cannot be null");
        }
        if (userRepository.existsByEmailOrUsername(registerDTO.getEmail(), registerDTO.getUsername())) {
            log.info("There is already a user with that email or username");
            throw new IllegalArgumentException("There is already a user with that email or username");
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setAge(registerDTO.getAge());
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.valueOf(registerDTO.getStatus()));
        user.setCreatedAt(LocalDateTime.now());
        user.setTickets(new ArrayList<>());
        user.setActive(false);
        user = userRepository.save(user);
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);
        return new UserWithoutPassDTO(user);
    }


    public UserWithoutTicketAndPassDTO logIn(String username, String password) {
        User user = userRepository.findByUsername(username)
          .orElseThrow(() -> new NotFoundException("User with username " + username + "is not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {

            // Create an Authentication object
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities() // Assuming you have a method to get user authorities
            );

            // Persist the SecurityContext into the HTTP session
            HttpSession session = ((VaadinServletRequest) VaadinService.getCurrentRequest())
                    .getHttpServletRequest()
                    .getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    new SecurityContextImpl(authentication)
            );

            // Set the authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            VaadinSession.getCurrent().setAttribute(User.class, user);
            createRoutes(user.getRole());
            return new UserWithoutTicketAndPassDTO(user);
        }
        throw new IllegalArgumentException("Username or Password incorrect");
    }

    public UserWithoutTicketAndPassDTO changePassword(EditUserPasswordDTO passwordDTO, int userId) throws IllegalArgumentException {
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords must match");
        }
        User user = userRepository.findById(userId).get();
        if (passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            return new UserWithoutTicketAndPassDTO(userRepository.save(user));
        }
        throw new IllegalArgumentException("Username or Password incorrect");
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(MAIN_VIEW_ROUTE);
        VaadinSession.getCurrent().getSession().invalidate();
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                null);
    }

    private void createRoutes(UserRole role) {
        getAuthorizedRoutes(role)
                .forEach(route ->
                        RouteConfiguration.forSessionScope().setRoute(
                                route.route, route.view, MainLayout.class));

    }

    @Secured("ROLE_ADMIN")
    public void deleteUser(int userId) throws UnauthorizedException{
        userRepository.deleteById(userId);
    }

    public void changeUserRole(int userId, UserRole role) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setRole(role);
        userRepository.save(user);
    }


    public UserRole getCurrentUserRole() {
        User currentUser = VaadinSession.getCurrent().getAttribute(User.class);
        if (currentUser != null) {
            return currentUser.getRole();
        }

//        // Fall back to Spring Security context
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof User) {
//            return ((User) authentication.getPrincipal()).getRole();
//        }

        // If neither is available, return default role (user) or throw exception
        return UserRole.USER; // or throw new UnauthorizedException("User not logged in");
    }

    public List<AuthorizedRoute> getAuthorizedRoutes(UserRole role) {
      var routes = new ArrayList<AuthorizedRoute>();
      routes.add(new AuthorizedRoute("main", "Main", MainView.class));
      routes.add(new AuthorizedRoute("login", "Login", LoginView.class));
      routes.add(new AuthorizedRoute("tickets", "Tickets", TicketView.class));
      routes.add(new AuthorizedRoute("projections", "Projections", ProjectionView.class));
      routes.add(new AuthorizedRoute("cinemas", "Cinemas", CinemaView.class));
      if (role.equals(UserRole.ADMIN)) {
        routes.add(new AuthorizedRoute("admin_panel", "Admin Panel", AdminView.class));
      }
      return routes;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}