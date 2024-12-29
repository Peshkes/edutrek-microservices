package com.telran.paymentservice.paymentInformation.persistence.current;


import com.telran.paymentservice.paymentInformation.persistence.AbstractPaymentInformation;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(schema = "current", name = "payment_information")
public class PaymentInfoEntity extends AbstractPaymentInformation {

    public PaymentInfoEntity( UUID studentNum, int paymentTypeId, double paymentUmount, String paymentDetails) {
        super( studentNum, paymentTypeId, paymentUmount, paymentDetails);
    }

}
