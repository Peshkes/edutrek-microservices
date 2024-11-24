package com.telran.contactservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;


@FeignClient(name = "CourseClient", url = "https://jsonplaceholder.typicode.com/")
public interface CourseFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/courses/exists/{id}")
    boolean existsById(@PathVariable UUID id);
}
