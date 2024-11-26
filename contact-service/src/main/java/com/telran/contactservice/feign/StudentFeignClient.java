package com.telran.contactservice.feign;


import com.telran.contactservice.config.FeignConfig;
import com.telran.contactservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;


@FeignClient(name = "StudentClient", url = "http://student-service:8080", configuration = FeignConfig.class)
public interface StudentFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/students/exists/{id}")
    boolean existsById(@PathVariable UUID id);

    @RequestMapping(method = RequestMethod.GET, value = "/students/exists/{phone}/{email}")
    AbstractStudentDto findByPhoneOrEmail(@PathVariable String phone, @PathVariable String email);

    @RequestMapping(method = RequestMethod.GET, value = "/students/{id}")
    AbstractStudentDto promoteContactToStudentById(@PathVariable UUID id, StudentsFromContactDataDto data);

    @RequestMapping(method = RequestMethod.POST, value = "/students/find_students")
    List<AbstractStudentDto> findStudents(@RequestBody FindStudentsDto data);

    @RequestMapping(method = RequestMethod.POST, value = "")
    AbstractStudentDto save(@PathVariable StudentsDataDto data);
}
