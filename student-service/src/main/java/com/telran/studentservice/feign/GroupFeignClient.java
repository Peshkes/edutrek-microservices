package com.telran.studentservice.feign;

import com.telran.studentservice.config.FeignConfig;
import com.telran.studentservice.dto.GetStudentsByGroupDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


@FeignClient(name = "GroupClient", url = "http://group-service:8080", configuration = FeignConfig.class)
public interface GroupFeignClient {

    @RequestMapping(method = RequestMethod.PUT, value = "/groups/archive/student/{id}")
    void archiveStudent(@PathVariable UUID id);

    @RequestMapping(method = RequestMethod.DELETE, value = "/groups/student/{id}")
    void deleteByStudentId(@PathVariable UUID id);

    @RequestMapping(method = RequestMethod.POST, value = "/groups/students")
    Map<UUID, List<GetStudentsByGroupDto>> getStudentsByGroup(@RequestBody Set<UUID> ids);
}
