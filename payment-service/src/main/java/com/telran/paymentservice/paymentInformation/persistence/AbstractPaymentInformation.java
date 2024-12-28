package com.telran.paymentservice.paymentInformation.persistence;


import com.telran.paymentservice.paymentInformation.persistence.current.PaymentInfoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class AbstractPaymentInformation {

    @Id
    @Column(name = "payment_id")
    @Setter(AccessLevel.NONE)
    private UUID paymentId;

    @Column(name = "student_id")
    private UUID studentId;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_type_id")
    private int paymentTypeId;

    @Column(name = "payment_amount")
    private int paymentAmount;

    @Column(name = "payment_details")
    private String paymentDetails;

    public AbstractPaymentInformation(UUID studentNum, int paymentTypeId, int paymentAmount, String paymentDetails) {
        this.paymentId = UUID.randomUUID();
        this.studentId = studentNum;
        this.paymentDate = LocalDate.now();
        this.paymentTypeId = paymentTypeId;
        this.paymentAmount = paymentAmount;
        this.paymentDetails = paymentDetails;
    }

    public AbstractPaymentInformation(PaymentInfoEntity paymentInfoEntity) {
        this.paymentId = paymentInfoEntity.getPaymentId();
        this.studentId = paymentInfoEntity.getStudentId();
        this.paymentDate = paymentInfoEntity.getPaymentDate();
        this.paymentTypeId = paymentInfoEntity.getPaymentTypeId();
        this.paymentAmount = paymentInfoEntity.getPaymentAmount();
        this.paymentDetails = paymentInfoEntity.getPaymentDetails();
    }
}
