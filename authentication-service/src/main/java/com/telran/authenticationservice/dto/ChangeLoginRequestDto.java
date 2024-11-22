package com.telran.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.telran.authenticationservice.error.ValidationErrors.LOGIN_MANDATORY;
import static com.telran.authenticationservice.error.ValidationErrors.LOGIN_SIZE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeLoginRequestDto {
    @NotBlank(message = LOGIN_MANDATORY)
    @Size(min = 3, max = 50, message = LOGIN_SIZE)
    private String login;
}
