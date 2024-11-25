package com.telran.paymentservice.paymentInformation.persistence;

import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

@NoArgsConstructor
public class PaymentInfoFilterSpecifications {


    public static  <E extends AbstractPaymentInformation> Specification<E> hasStudentId(UUID studentId) {
        return (r, q, cb) -> cb.equal(r.get("studentId"), studentId);
    }


    public static  <E extends AbstractPaymentInformation> Specification<E> getPaymentsSpecifications(UUID studentId) {
        Specification<E> specs = Specification.where(null);
        if (studentId != null)
            specs = specs.and(hasStudentId(studentId));
        return specs;
    }


}
