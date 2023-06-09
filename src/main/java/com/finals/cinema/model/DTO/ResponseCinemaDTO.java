package com.finals.cinema.model.DTO;

import com.finals.cinema.model.entity.Cinema;
import com.finals.cinema.model.entity.Hall;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class ResponseCinemaDTO {

    private int id;
    private String name;
    private String city;
    private List<HallWithoutCinemaDTO> halls;

    public ResponseCinemaDTO(Cinema c){
        this.id = c.getId();
        this.name = c.getName();
        this.city = c.getCity();
        halls = new ArrayList<>();
        for (Hall h: c.getHalls()) {
            halls.add(new HallWithoutCinemaDTO(h));
        }
    }
}
