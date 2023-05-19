package com.finals.cinema.model.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class RequestProjectionDTO {

    @NotNull(message = "Please fill all necessary fields")
    @Min(value = 1,message = "Movie Id cannot be less than 1")
    private Integer movieId;
    @NotNull(message = "Please fill all necessary fields")
    @Min(value = 1,message = "Hall Id cannot be less than 1")
    private Integer hallId;
    @NotBlank(message = "Please fill all necessary fields")
    @NotNull(message = "Please fill all necessary fields")
    private String time;
    private LocalDateTime startAt;

}
