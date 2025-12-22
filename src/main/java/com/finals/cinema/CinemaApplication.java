package com.finals.cinema;


import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.sql.SQLException;

@SpringBootApplication
//@Configuration("login")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@Theme("flowcrmtutorial")
public class CinemaApplication implements AppShellConfigurator {

    public static void main(String[] args) throws SQLException {
        
        SpringApplication.run(CinemaApplication.class, args);
    }
}