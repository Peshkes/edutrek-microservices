package com.telran.authenticationservice.dto;

import static com.telran.authenticationservice.error.ValidationErrors.*;

import com.telran.authenticationservice.persistence.Roles;
import com.telran.authenticationservice.validation.ValidRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddNewAccountRequestDto {
    @NotNull(message = EMAIL_MANDATORY)
    @NotEmpty(message = EMAIL_NOT_EMPTY)
    @Email(message = EMAIL_INVALID_FORMAT)
    private String email;

    @NotNull(message = NAME_MANDATORY)
    @NotEmpty(message = NAME_NOT_EMPTY)
    @Size(min = 2, max = 50, message = NAME_SIZE)
    private String name;

    @NotEmpty(message = ROLES_MANDATORY)
    private List<@NotNull(message = ROLE_NOT_NULL) @ValidRole(message = ROLE_INVALID) Roles> roles;

}