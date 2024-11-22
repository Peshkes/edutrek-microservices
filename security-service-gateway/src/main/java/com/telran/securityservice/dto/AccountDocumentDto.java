package com.telran.securityservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDocumentDto {
    private UUID accountId;
    private String login;
    private String email;
    private String name;
    private LocalDate lastPasswordChange;
    private LinkedList<String> lastPasswords;
    private String password;
    private List<Roles> roles;
}