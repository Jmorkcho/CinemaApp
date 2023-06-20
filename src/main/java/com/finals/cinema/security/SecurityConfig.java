package com.finals.cinema.security;

import com.finals.cinema.view.LoginView;
import com.finals.cinema.view.MainLayout;
import com.finals.cinema.view.MainView;
import com.vaadin.flow.server.BootstrapException;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig
        extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Delegating the responsibility of general configurations
        // of http security to the super class. It's configuring
        // the followings: Vaadin's CSRF protection by ignoring
        // framework's internal requests, default request cache,
        // ignoring public views annotated with @AnonymousAllowed,
        // restricting access to other views/endpoints, and enabling
        // ViewAccessChecker authorization.
        // You can add any possible extra configurations of your own
        // here (the following is just an example):

        // http.rememberMe().alwaysRemember(false);

        // Configure your static resources with public access before calling
        // super.configure(HttpSecurity) as it adds final anyRequest matcher
        http.httpBasic().disable().formLogin().disable().csrf().disable().authorizeHttpRequests().anyRequest().permitAll();
        //http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/"))
        //        .permitAll();

       // super.configure(http);

        // This is important to register your login view to the
        // view access checker mechanism:
//        try {
//            setLoginView(http, LoginView.class, "/");
//        } catch (Exception e) {
//            System.out.println(e);
//        }


    }

     //DaoAuthenticationProvider putka = new DaoAuthenticationProvider();
    //}

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Customize your WebSecurity configuration.
        super.configure(web);
    }

    /**
     * Demo UserDetailsManager which only provides two hardcoded
     * in memory users and their roles.
     * NOTE: This shouldn't be used in real world applications.
     */

}