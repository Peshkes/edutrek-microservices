package com.telran.lecturerservice.feign;

import com.telran.lecturerservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient(name = "LogClient", url = "http://log-service:8080", path = "/logs", configuration = FeignConfig.class)
public interface LogClient {

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    void deleteById(@PathVariable UUID id);

    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    void add(@PathVariable UUID id, @RequestBody String text);
}
