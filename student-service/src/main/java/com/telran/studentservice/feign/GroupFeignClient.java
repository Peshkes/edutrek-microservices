package com.telran.studentservice.feign;

import com.telran.studentservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;


@FeignClient(name = "GroupClient", url = "http://group-service:8080", configuration = FeignConfig.class)
public interface GroupFeignClient {

    @RequestMapping(method = RequestMethod.PUT, value = "/groups/archive/studentid/{id}")
    void archiveStudents(@PathVariable UUID id);

    @RequestMapping(method = RequestMethod.DELETE, value = "/groups/delete/studentid/{id}/{iscurrent}")
    void deleteByStudentId(@PathVariable UUID id, boolean iscurrent);
}