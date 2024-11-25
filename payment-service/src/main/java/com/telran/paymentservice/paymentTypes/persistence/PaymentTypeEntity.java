package com.telran.paymentservice.paymentTypes.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@Table(schema = "current", name = "payment_types")
@NoArgsConstructor
public class PaymentTypeEntity {
    @Id
    @Column(name = "payment_type_id")
    private int paymentTypeId;
    @Column(name = "payment_type_name")
    private String paymentTypeName;

    public PaymentTypeEntity(String paymentTypeName) {
        this.paymentTypeName = paymentTypeName;
    }
}