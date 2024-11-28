package com.telran.paymentservice.paymentInformation.service;


import com.telran.paymentservice.error.DatabaseException.*;
import com.telran.paymentservice.error.ShareException.*;
import com.telran.paymentservice.logging.Loggable;
import com.telran.paymentservice.paymentInformation.dto.PaymentInfoDataDto;
import com.telran.paymentservice.paymentInformation.dto.PaymentsInfoSearchDto;

import com.telran.paymentservice.paymentInformation.feign.StudentsFeignClient;
import com.telran.paymentservice.paymentInformation.persistence.AbstractPaymentInformation;
import com.telran.paymentservice.paymentInformation.persistence.archive.PaymentInfoArchiveEntity;
import com.telran.paymentservice.paymentInformation.persistence.archive.PaymentInfoArchiveRepository;
import com.telran.paymentservice.paymentInformation.persistence.current.PaymentInfoEntity;
import com.telran.paymentservice.paymentInformation.persistence.current.PaymentInfoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.telran.paymentservice.paymentInformation.persistence.PaymentInfoFilterSpecifications.getPaymentsSpecifications;


@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"payment_information"})
public class PaymentInfoService {

    private final PaymentInfoRepository repository;
    private final PaymentInfoArchiveRepository archiveRepository;
    private final StudentsFeignClient studentsFeignClient;

    @Loggable
    public AbstractPaymentInformation getByPaymentId(UUID paymentId) {
        return repository.findByPaymentId(paymentId).or(() -> archiveRepository.findById(paymentId)).orElseThrow(() -> new PaymentInfoNotFoundException(paymentId.toString()));
    }

    @Loggable
    @SuppressWarnings("unchecked")
    public PaymentsInfoSearchDto getByStudentId(int page, int pageSize, UUID studentId) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Specification<PaymentInfoEntity> specs = getPaymentsSpecifications(studentId);
        Page<? extends AbstractPaymentInformation> pageFoundPayments = repository.findAll(specs, pageable);
        List<AbstractPaymentInformation> foundPayments = (List<AbstractPaymentInformation>)pageFoundPayments.getContent();
        if (foundPayments.size() < pageSize) {
            Specification<PaymentInfoArchiveEntity> archiveSpecs = getPaymentsSpecifications(studentId);
            Page<? extends AbstractPaymentInformation> pageFoundArchivePayments = archiveRepository.findAll(archiveSpecs, PageRequest.of(page, pageSize - foundPayments.size()));
            List<AbstractPaymentInformation> foundArchivePayments = (List<AbstractPaymentInformation>) pageFoundArchivePayments.getContent();
            if (!foundArchivePayments.isEmpty())
                foundPayments.addAll(foundArchivePayments);
        }
        return new PaymentsInfoSearchDto(foundPayments, page, pageSize, foundPayments.size());
    }

    @Transactional
    public void deleteByStudentId(int page, int pageSize, UUID studentId) {
        List<AbstractPaymentInformation> payments = getByStudentId(page,pageSize,studentId).paymentsInfo();
        payments.forEach(p -> movePaymentsToArchive(p.getPaymentId()));
    }


    @Loggable
    @Transactional
    @CacheEvict(key = "{'getAll'}")
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 2000))
    public void addEntity(PaymentInfoDataDto paymentInfoDtoData) {
        if (!studentsFeignClient.existsById(paymentInfoDtoData.getStudentId())) {
            throw new StudentNotFoundException(String.valueOf(paymentInfoDtoData.getStudentId()));
        }
        try {
            repository.save(new PaymentInfoEntity(
                    paymentInfoDtoData.getStudentId(),
                    paymentInfoDtoData.getPaymentTypeId(),
                    paymentInfoDtoData.getPaymentUmount(),
                    paymentInfoDtoData.getPaymentDetails()));
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    @CachePut(key = "#id")
    public void deleteById(UUID id) {
        if (!repository.existsById(id))
            throw new PaymentInfoNotFoundException(String.valueOf(id));
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
    }

    @Loggable
    @Transactional
    @CachePut(key = "#id")
    public void updateById(UUID id, PaymentInfoDataDto paymentInfoDataDto) {
        AbstractPaymentInformation paymentEntity = repository.findByPaymentId(id).or(() -> archiveRepository.findById(id)).orElseThrow(() -> new PaymentInfoNotFoundException(id.toString()));
        updateEntity(paymentInfoDataDto, paymentEntity);
    }

    private <T extends AbstractPaymentInformation> void updateEntity(PaymentInfoDataDto paymentInfoDataDto, T entity) {
        entity.setStudentId(paymentInfoDataDto.getStudentId());
        entity.setPaymentDate(paymentInfoDataDto.getPaymentDate());
        entity.setPaymentTypeId(paymentInfoDataDto.getPaymentTypeId());
        entity.setPaymentUmount(paymentInfoDataDto.getPaymentUmount());
        entity.setPaymentDetails(paymentInfoDataDto.getPaymentDetails());
    }

    @Loggable
    @Transactional
    @CachePut(key = "#id")
    public void movePaymentsToArchive(UUID id) {
        PaymentInfoEntity paymentEntity = repository.findById(id).orElseThrow(() -> new PaymentInfoNotFoundException(id.toString()));
        PaymentInfoArchiveEntity paymentArchiveEntity = new PaymentInfoArchiveEntity(paymentEntity);
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseDeletingException(e.getMessage());
        }
        try {
            archiveRepository.save(paymentArchiveEntity);
        } catch (Exception e) {
            throw new DatabaseAddingException(e.getMessage());
        }
    }
}
