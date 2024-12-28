package com.telran.paymentservice.paymentInformation.persistence;

import com.telran.paymentservice.paymentInformation.dto.PaymentInfoReturnDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@NoRepositoryBean
public interface IPaymentInfoRepository<T extends  AbstractPaymentInformation> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {

    Optional<AbstractPaymentInformation> findByPaymentId(UUID paymentInfoId);

    @Modifying
    void deleteAllByStudentId(UUID studentId);

    Page<? extends AbstractPaymentInformation> findByStudentId(UUID studentId, Pageable pageable);

    @Query("SELECT new com.telran.paymentservice.paymentInformation.dto.PaymentInfoReturnDto(pie.studentId, pie.paymentAmount) " +
            "FROM PaymentInfoEntity pie WHERE pie.studentId IN :studentIds")
    List<PaymentInfoReturnDto> findAllByStudentId(Set<UUID> studentIds);
}
