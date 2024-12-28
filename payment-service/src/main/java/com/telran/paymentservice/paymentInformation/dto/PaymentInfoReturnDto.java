package com.telran.paymentservice.paymentInformation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInfoReturnDto {
    private UUID studentId;
    private int paymentAmount;
}
