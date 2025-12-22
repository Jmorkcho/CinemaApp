package com.finals.cinema.service;

import com.finals.cinema.model.DTO.ResponseHallDTO;
import com.finals.cinema.repository.CinemaRepository;
import com.finals.cinema.repository.HallRepository;
import com.finals.cinema.util.exceptions.NotFoundException;
import com.finals.cinema.model.DTO.RequestHallDTO;
import com.finals.cinema.model.entity.Cinema;
import com.finals.cinema.model.entity.Hall;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository hallRepository;
    private final CinemaRepository cinemaRepository;

    public ResponseHallDTO getHallById(int id) {
        Optional<Hall> sHall = hallRepository.findById(id);
        if (sHall.isEmpty()) {
            throw new NotFoundException("Hall not found");
        }
        return new ResponseHallDTO(sHall.get());
    }

    public ResponseHallDTO addHall(RequestHallDTO requestHallDTO) {

        Optional<Cinema> sCinema = cinemaRepository.findById(requestHallDTO.getCinemaId());
        if (sCinema.isEmpty()) {
            throw new NotFoundException("Cinema is not found");
        }
        Cinema cinema = sCinema.get();
        if (cinemaHasHall(cinema,requestHallDTO.getNumber())) {
            throw new IllegalArgumentException("There is already a hall with that number in that cinema");
        }
        Hall hall = new Hall();
        hall.setNumber(requestHallDTO.getNumber());
        hall.setCapacity(requestHallDTO.getCapacity());
        hall.setCinema(cinema);
        return new ResponseHallDTO(hallRepository.save(hall));
    }

    public ResponseHallDTO removeHall(int hallId) {
        Optional<Hall> sHall = hallRepository.findById(hallId);
        if (sHall.isEmpty()) {
            throw new NotFoundException("Hall does not exist");
        }
        ResponseHallDTO deletedHall = new ResponseHallDTO(sHall.get());
        hallRepository.deleteById(hallId);
        return deletedHall;
    }

    public ResponseHallDTO editHall(RequestHallDTO requestHallDTO, int hallId) {
        Optional<Cinema> sCinema = cinemaRepository.findById(requestHallDTO.getCinemaId());
        if (sCinema.isEmpty()) {
            throw new NotFoundException("Cinema is not found");
        }
        Optional<Hall> sHall = hallRepository.findById(hallId);
        if (!cinemaHasHall(sCinema.get(), sHall)) {
            throw new NotFoundException("No hall with that id in this cinema");
        }
        Hall hall = sHall.get();
        if (hall.getNumber() == requestHallDTO.getNumber() && hall.getCapacity() == requestHallDTO.getCapacity()) {
            throw new IllegalArgumentException("You need to change the values for an edit");
        }
        hall.setNumber(requestHallDTO.getNumber());
        hall.setCapacity(requestHallDTO.getCapacity());
        return new ResponseHallDTO(hallRepository.save(hall));
    }

    private boolean cinemaHasHall(Cinema cinema, Optional<Hall> hall) {
        return hall.isPresent() && cinema.getHalls().contains(hall.get());
    }

    private boolean cinemaHasHall(Cinema cinema, int hallNumber) {
        for (Hall h : cinema.getHalls()) {
            if (h.getNumber() == hallNumber){
                return true;
            }
        }
        return false;
    }
}
