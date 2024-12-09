package com.telran.groupservice.feign;

import com.telran.groupservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "LecturerClient", url = "http://lecturer-service:8080", configuration = FeignConfig.class)
public interface LecturerClient {
    @GetMapping("/lecturers/exists/{id}")
    boolean existsById(@PathVariable("id") UUID id);
}
