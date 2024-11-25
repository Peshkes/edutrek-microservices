package com.telran.studentservice.feign;

import com.telran.studentservice.dto.PaymentsInfoSearchDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;


@FeignClient(name = "PaymentsClient", url = "http://payment-service:8080")
public interface PaymentsFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/payments/studentid/{id}")
    PaymentsInfoSearchDto getPaymentByStudentId(@PathVariable UUID id);

    @RequestMapping(method = RequestMethod.GET, value = "/payments/archive/{id}")
    void moveToArchiveById(@PathVariable UUID id);

}
