package com.telran.authenticationservice.dto;

import com.telran.authenticationservice.persistence.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicAccountDataDto {
    private UUID id;
    private String email;
    private String login;
    private String name;
    private List<Roles> roles;
}
