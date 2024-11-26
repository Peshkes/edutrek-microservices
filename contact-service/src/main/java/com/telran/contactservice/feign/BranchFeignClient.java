package com.telran.contactservice.feign;

import com.telran.contactservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "BranchClient", url = "http://branch-service:8080", configuration = FeignConfig.class)
public interface BranchFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/branches/exists/{id}")
    boolean existsById(@PathVariable int id);
}
