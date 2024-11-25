package com.telran.mailingservice.controller;

import com.telran.mailingservice.dto.RegistrationEmailDto;
import com.telran.mailingservice.service.EmailService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/mailing")
public class MailingController {

    EmailService emailService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.OK)
    public void sendRegistrationEmail(@RequestBody RegistrationEmailDto registrationEmailDto) {
        try{
            emailService.sendRegistrationEmail(registrationEmailDto.getEmail(), registrationEmailDto.getLogin(), registrationEmailDto.getPassword() );
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
