package com.finals.cinema;


import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


import java.sql.SQLException;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
//@Configuration("login")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@Theme("flowcrmtutorial")
@EnableWebSecurity
public class CinemaApplication implements AppShellConfigurator {

    public static void main(String[] args) throws SQLException {
        
        SpringApplication.run(CinemaApplication.class, args);
        //Thread cleaner = new Thread(new OldProjectionsCleaner());
        //cleaner.setDaemon(true);
        //cleaner.start();
    }
}
