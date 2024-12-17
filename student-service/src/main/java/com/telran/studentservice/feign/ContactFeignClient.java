package com.telran.studentservice.feign;

import com.telran.studentservice.config.FeignConfig;
import com.telran.studentservice.dto.AbstractContactsDto;
import com.telran.studentservice.dto.StudentsFromContactDataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;


@FeignClient(name = "ContactClient", url = "http://contact-service:8080", configuration = FeignConfig.class)
public interface ContactFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/contacts/find/{phone}/{email}")
    AbstractContactsDto findByPhoneOrEmail(@PathVariable String phone, @PathVariable String email);

    @RequestMapping(method = RequestMethod.GET, value = "/contacts/exists/{phone}/{email}")
    boolean existsByPhoneOrEmail(@PathVariable String phone, @PathVariable String email);

    @RequestMapping(method = RequestMethod.GET, value = "/contacts/{id}")
    AbstractContactsDto promoteContactToStudentById(@PathVariable UUID id, StudentsFromContactDataDto data);

    @RequestMapping(method = RequestMethod.DELETE, value = "/contacts/{phone}/{email}")
    AbstractContactsDto findByPhoneAndEmailAndDelete(@PathVariable String phone, @PathVariable String email);
}
