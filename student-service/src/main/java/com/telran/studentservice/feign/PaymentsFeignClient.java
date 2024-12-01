package com.telran.studentservice.feign;

import com.telran.studentservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;


@FeignClient(name = "PaymentsClient", url = "http://payment-service:8080", configuration = FeignConfig.class)
public interface PaymentsFeignClient {

    @RequestMapping(method = RequestMethod.DELETE, value = "/payments/studentid/{id}")
    void deletePaymentByStudentId(@PathVariable UUID id);

    @RequestMapping(method = RequestMethod.GET, value = "/payments/archive/{id}")
    void moveToArchiveById(@PathVariable UUID id);

}
