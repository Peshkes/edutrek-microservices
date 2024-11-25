package com.telran.paymentservice.paymentTypes.controller;


import com.telran.paymentservice.paymentTypes.persistence.PaymentTypeEntity;
import com.telran.paymentservice.paymentTypes.service.PaymentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/payment_types")
@RequiredArgsConstructor
public class PaymentTypeController {

    private final PaymentTypeService service;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentTypeEntity> getAllStatuses() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentTypeEntity getById(@PathVariable int id) {
            return service.getById(id);
    }
}
