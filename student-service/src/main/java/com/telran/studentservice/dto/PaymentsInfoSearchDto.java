package com.telran.studentservice.dto;


import java.util.List;

public record PaymentsInfoSearchDto(List<AbstractPaymentInformationDto> paymentsInfo, int page, int pageSize, int size) {}