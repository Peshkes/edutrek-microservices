package com.telran.paymentservice.paymentInformation.persistence.current;


import com.telran.paymentservice.paymentInformation.persistence.IPaymentInfoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentInfoRepository extends IPaymentInfoRepository<PaymentInfoEntity>,JpaRepository<PaymentInfoEntity, UUID>, JpaSpecificationExecutor<PaymentInfoEntity> {

    Optional<List<PaymentInfoEntity>> findByStudentId(UUID studentId);

}
