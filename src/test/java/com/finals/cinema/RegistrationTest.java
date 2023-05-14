package com.finals.cinema;
import static org.junit.Assert.*;

import com.finals.cinema.model.DTO.RequestCinemaDTO;
import com.finals.cinema.service.CinemaService;
import com.finals.cinema.util.exceptions.UnauthorizedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.finals.cinema.model.DTO.RegisterDTO;
import com.finals.cinema.service.UserService;
import com.finals.cinema.util.exceptions.BadRequestException;

@RunWith(SpringJUnit4ClassRunner.class)
    @SpringBootTest
    public class RegistrationTest {

        @Autowired
        protected CinemaService cinemaService;
        @Autowired
        protected UserService userService;

        @Test
        public void testRegistrationUser() throws BadRequestException {
            int regSize = userService.findAll().size();
            userService.registerUser(new RegisterDTO("Tester","MalkiqMuk1","MalkiqMuk1","bigboy@gmail.com","Vasil","Aprilov",17,"student"));
            int regSize2 = userService.findAll().size();
            boolean areSame = false;
            if(regSize == regSize2) areSame = true;
            assertFalse(areSame);
        }

        @Test
        public void toAddCinema() throws BadRequestException, UnauthorizedException {
            int regSize = cinemaService.getAllCinemas().size();
            cinemaService.addCinema(new RequestCinemaDTO("Test Cinema","New York"),33);
            int regSize2 = cinemaService.getAllCinemas().size();
            boolean areSame = false;
            if(regSize == regSize2) areSame = true;
            assertFalse(areSame);
        }
    }

