package com.telran.studentservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "BranchClient", url = "https://jsonplaceholder.typicode.com/")
public interface BranchFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/branches/exists/{id}")
    boolean existsById(@PathVariable int id);
}
