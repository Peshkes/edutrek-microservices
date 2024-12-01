package com.telran.lecturerservice.feign;

import com.telran.lecturerservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "BranchClient", url = "http://branch-service:8080", path = "/branches", configuration = FeignConfig.class)
public interface BranchClient {
    @RequestMapping(method = RequestMethod.GET, value = "/exists/{id}")
    boolean existsById(@PathVariable int id);

    @RequestMapping(method = RequestMethod.GET, value = "/name/{id}")
    String getNameById(@PathVariable int id);
}

