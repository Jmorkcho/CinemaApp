package com.finals.cinema.model.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class addMovieDTO {

    @NotBlank(message = "Please fill all necessary fields")
    @NotNull(message = "Please fill all necessary fields")
    private String title;
    @NotNull(message = "Please fill all necessary fields")
    @Min(value = 3,message = "Age restriction cannot be less than 3")
    private Integer ageRestriction;
    @NotNull(message = "Please fill all necessary fields")
    @Min(value = 1,message = "Genre Id cannot be less than 1")
    private Integer genreId;
}
