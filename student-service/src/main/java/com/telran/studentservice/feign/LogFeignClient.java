package com.telran.studentservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;


@FeignClient(name = "LogClient", url = "https://jsonplaceholder.typicode.com/")
public interface LogFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/logs/{id}")
    void deleteById(@PathVariable UUID id);
}
