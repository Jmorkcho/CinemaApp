package com.finals.cinema.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class RequestCinemaDTO {

    @NotBlank(message = "Please fill all necessary fields")
    @NotNull(message = "Please fill all necessary fields")
    @Pattern(regexp = "^(\\w+\\s?)*$",message = "Name must contain only letters and numbers")
    private String name;
    @NotBlank(message = "Please fill all necessary fields")
    @NotNull(message = "Please fill all necessary fields")
    @Pattern(regexp = "^([a-zA-Z]+\\s?)*$",message = "City must contain only letters")
    private String city;
}