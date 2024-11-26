package com.telran.contactservice.feign;

import com.telran.contactservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;


@FeignClient(name = "CourseClient", url = "http://course-service:8080", configuration = FeignConfig.class)
public interface CourseFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/courses/exists/{id}")
    boolean existsById(@PathVariable UUID id);
}
