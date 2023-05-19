package com.finals.cinema.model.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@NoArgsConstructor
@Getter
@Setter
public class LoginDTO {

    @NotBlank(message = "Please fill all necessary fields")
    @NotNull(message = "Please fill all necessary fields")
    private String username;
    @NotBlank(message = "Please fill all necessary fields")
    @NotNull(message = "Please fill all necessary fields")
    private String password;
}
