package com.telran.authenticationservice.feign;

import com.telran.authenticationservice.dto.RegistrationEmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "MailingClient", url = "https://jsonplaceholder.typicode.com")
public interface MailingClient {

    @PostMapping("/mailing/registration")
    void sendRegistrationEmail(@RequestBody RegistrationEmailDto data);


}
