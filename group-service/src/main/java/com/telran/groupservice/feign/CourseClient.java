package com.telran.groupservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "CourseClient", url = "https://jsonplaceholder.typicode.com")
public interface CourseClient {
    @GetMapping("/courses/exists/{id}")
    Boolean existsById(@PathVariable("id") UUID id);
}
