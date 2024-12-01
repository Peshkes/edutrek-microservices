package com.telran.paymentservice.paymentInformation.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface IPaymentInfoRepository<T extends  AbstractPaymentInformation> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {
    Optional<AbstractPaymentInformation> findByPaymentId(UUID paymentInfoId);



    @Modifying
    void deleteByStudentId(UUID studentId);
}
