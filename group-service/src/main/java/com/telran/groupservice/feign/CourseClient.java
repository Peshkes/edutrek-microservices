package com.telran.groupservice.feign;

import com.telran.groupservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "CourseClient", url = "http://course-service:8080", configuration = FeignConfig.class)
public interface CourseClient {
    @GetMapping("/courses/exists/{id}")
    Boolean existsById(@PathVariable("id") UUID id);
}
