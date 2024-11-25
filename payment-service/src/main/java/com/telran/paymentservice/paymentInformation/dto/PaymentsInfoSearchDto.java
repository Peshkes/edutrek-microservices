package com.telran.paymentservice.paymentInformation.dto;


import com.telran.paymentservice.paymentInformation.persistence.AbstractPaymentInformation;

import java.util.List;

public record PaymentsInfoSearchDto(List<AbstractPaymentInformation> paymentsInfo, int page, int pageSize, int size) {}