package com.telran.paymentservice.paymentInformation.persistence.archive;


import com.telran.paymentservice.paymentInformation.persistence.AbstractPaymentInformation;
import com.telran.paymentservice.paymentInformation.persistence.current.PaymentInfoEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(schema = "archive", name = "payment_information")
public class PaymentInfoArchiveEntity extends AbstractPaymentInformation {

    public PaymentInfoArchiveEntity(PaymentInfoEntity paymentInfoEntity) {
        super(paymentInfoEntity);
    }
}
