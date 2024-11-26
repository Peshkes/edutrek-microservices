package com.telran.paymentservice.paymentInformation.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;


@FeignClient(name = "StudentsClient", url = "http://student-service:8080")
public interface StudentsFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/students/exists/{id}")
    boolean existsById(@PathVariable UUID id);

}
