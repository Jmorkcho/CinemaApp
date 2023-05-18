package com.finals.cinema.controller;

import com.finals.cinema.service.HallService;
import com.finals.cinema.model.DTO.ResponseHallDTO;
import com.finals.cinema.util.exceptions.BadRequestException;
import com.finals.cinema.util.exceptions.UnauthorizedException;
import com.finals.cinema.model.DTO.RequestHallDTO;
import com.finals.cinema.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import javax.validation.Valid;

@Component
@RestController
public class HallController extends AbstractController {

    @Autowired
    private HallService hallService;

    @GetMapping(value = "/hall/{hall_id}")
    public ResponseHallDTO getHallById(@PathVariable(name = "hall_id") int hallId) {
        return hallService.getHallById(hallId);
    }

    @PostMapping(value = "/halls")
    public ResponseHallDTO addHall(@Valid @RequestBody RequestHallDTO requestHallDTO, HttpSession ses) throws BadRequestException, UnauthorizedException {
        User user = sessionManager.getLoggedUser(ses);
        return hallService.addHall(requestHallDTO, user.getId());
    }

    @DeleteMapping(value = "/halls/{hall_id}")
    public ResponseHallDTO deleteHall(@PathVariable(name = "hall_id") int hallId, HttpSession ses) throws UnauthorizedException {
        User user = sessionManager.getLoggedUser(ses);
        return hallService.removeHall(hallId, user.getId());
    }

    @PutMapping(value = "/halls/{hall_id}")
    public ResponseHallDTO editHall(@PathVariable(name = "hall_id") int hallId,
                                    @Valid @RequestBody RequestHallDTO requestHallDTO, HttpSession ses) throws BadRequestException, UnauthorizedException {
        User user = sessionManager.getLoggedUser(ses);
        return hallService.editHall(requestHallDTO, hallId, user.getId());
    }
}


