package com.finals.cinema.model.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@NoArgsConstructor
@Getter
@Setter
public class EditUserPasswordDTO {

    @NotBlank(message = "Please fill all necessary fields")
    @NotNull(message = "Please fill all necessary fields")
    private String oldPassword;
    @NotBlank(message = "Please fill all necessary fields")
    @NotNull(message = "Please fill all necessary fields")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$",message = "Password must be between 8 and 20 symbols and must contain at least one upper and lower case letters and a number")
    private String newPassword;
    @NotNull(message = "Please fill all necessary fields")
    private String confirmPassword;
}
