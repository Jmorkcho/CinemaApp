package com.finals.cinema;


import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;


import java.sql.SQLException;

@SpringBootApplication
@Configuration("login")
@Theme("flowcrmtutorial")
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class CinemaApplication {

    public static void main(String[] args) throws SQLException {
        
        SpringApplication.run(CinemaApplication.class, args);
        //Thread cleaner = new Thread(new OldProjectionsCleaner());
        //cleaner.setDaemon(true);
        //cleaner.start();
    }

}
