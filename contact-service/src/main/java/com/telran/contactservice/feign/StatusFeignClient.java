package com.telran.contactservice.feign;


import com.telran.contactservice.dto.StatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "StatusesClient", url = "http://status-service:8080")
public interface StatusFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/statuses/{id}")
    StatusDto getStatusById(@PathVariable int id);

    @RequestMapping(method = RequestMethod.GET, value = "/statuses/exists/{id}")
    boolean existsById(@PathVariable int id);

    @RequestMapping(method = RequestMethod.GET, value = "/statuses/find_by_status_name/{status}")
    StatusDto findStatusEntityByStatusName(@PathVariable String status);

}
