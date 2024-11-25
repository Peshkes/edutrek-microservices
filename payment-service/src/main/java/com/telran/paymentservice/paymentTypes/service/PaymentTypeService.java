package com.telran.paymentservice.paymentTypes.service;


import com.telran.paymentservice.error.ShareException.*;
import com.telran.paymentservice.logging.Loggable;
import com.telran.paymentservice.paymentTypes.persistence.PaymentTypeEntity;
import com.telran.paymentservice.paymentTypes.persistence.PaymentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
@RequiredArgsConstructor
public class PaymentTypeService {

    private final PaymentTypeRepository repository;

    @Loggable
    public List<PaymentTypeEntity> getAll() {
        return repository.findAll();
    }

    @Loggable
    public PaymentTypeEntity getById(int paymentTypeId) {
        return repository.findById(paymentTypeId).orElseThrow(() -> new StatusNotFoundException(paymentTypeId));
    }
}
