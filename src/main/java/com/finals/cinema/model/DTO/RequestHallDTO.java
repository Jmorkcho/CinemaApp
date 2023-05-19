package com.finals.cinema.model.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class RequestHallDTO {

    @NotNull(message = "Please fill all necessary fields")
    @Min(value = 1,message = "Hall number cannot be less than 1")
    private Integer number;
    @NotNull(message = "Please fill all necessary fields")
    @Min(value = 50,message = "Capacity cannot be less than 50")
    private Integer capacity;
    @NotNull(message = "Please fill all necessary fields")
    @Min(value = 1,message = "Cinema Id cannot be less than 1")
    private Integer cinemaId;

}
