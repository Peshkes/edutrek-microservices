package com.telran.contactservice.feign;

import com.telran.contactservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;


@FeignClient(name = "LogClient", url = "http://log-service:8080", configuration = FeignConfig.class)
public interface LogFeignClient {

    @RequestMapping(method = RequestMethod.DELETE, value = "/logs/{id}")
    void deleteById(@PathVariable UUID id);

    @RequestMapping(method = RequestMethod.POST, value = "/logs/{id}")
    void add(@PathVariable UUID id, @RequestBody String text);
}
