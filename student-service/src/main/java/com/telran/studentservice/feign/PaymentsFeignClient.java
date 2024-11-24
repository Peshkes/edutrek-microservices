package com.telran.studentservice.feign;

import com.telran.studentservice.dto.AbstractPaymentInformationDto;
import com.telran.studentservice.dto.PaymentsInfoSearchDto;
import com.telran.studentservice.dto.StatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;


@FeignClient(name = "PaymentsClient", url = "https://jsonplaceholder.typicode.com/")
public interface PaymentsFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/payments/studentid/{id}")
    PaymentsInfoSearchDto getPaymentByStudentId(@PathVariable UUID id);

    @RequestMapping(method = RequestMethod.GET, value = "/payments/archive/{id}")
    void moveToArchiveById(@PathVariable UUID id);

}
