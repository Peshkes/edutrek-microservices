package com.telran.studentservice.dto;


import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class AbstractPaymentInformationDto {
    private UUID paymentId;
    private UUID studentId;
    private LocalDate paymentDate;
    private int paymentTypeId;
    private int paymentUmount;
    private String paymentDetails;

}
