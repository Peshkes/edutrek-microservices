package com.telran.securityservice.feign;

import com.telran.securityservice.config.FeignConfig;
import com.telran.securityservice.dto.AccountDocumentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "AuthenticationClient", url = "http://authentication-service:8080", path = "/secret", configuration = FeignConfig.class)
public interface AuthenticationClient {
    @GetMapping("/id/{id}")
    AccountDocumentDto findById(@PathVariable("id") UUID id);

    @GetMapping("/login/{login}")
    AccountDocumentDto findByLogin(@PathVariable("login") String login);
}