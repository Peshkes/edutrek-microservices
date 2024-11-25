package com.telran.groupservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "LecturerClient", url = "http://lecturer-service:8080")
public interface LecturerClient {
    @GetMapping("/lecturers/exists/{id}")
    Boolean existsById(@PathVariable("id") UUID id);
}
