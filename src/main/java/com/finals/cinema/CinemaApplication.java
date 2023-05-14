package com.finals.cinema;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.sql.SQLException;

@SpringBootApplication
public class CinemaApplication {

    public static void main(String[] args) throws SQLException {
        
        SpringApplication.run(CinemaApplication.class, args);
        //Thread cleaner = new Thread(new OldProjectionsCleaner());
        //cleaner.setDaemon(true);
        //cleaner.start();
    }

}
