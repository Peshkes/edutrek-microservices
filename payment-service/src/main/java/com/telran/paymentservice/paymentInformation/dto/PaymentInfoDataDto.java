package com.telran.paymentservice.paymentInformation.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInfoDataDto {

    @Column(name = "student_id")
    private UUID studentId;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_type_id")
    private int paymentTypeId;

    @Column(name = "payment_amount")
    private BigDecimal paymentAmount;

    @Column(name = "payment_details")
    private String paymentDetails;
}
