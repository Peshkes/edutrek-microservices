package com.telran.paymentservice.paymentInformation.controller;


import com.telran.paymentservice.paymentInformation.dto.PaymentInfoDataDto;
import com.telran.paymentservice.paymentInformation.dto.PaymentsInfoSearchDto;
import com.telran.paymentservice.paymentInformation.persistence.AbstractPaymentInformation;
import com.telran.paymentservice.paymentInformation.service.PaymentInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentInfoController {

    private final PaymentInfoService paymentInfoService;


    @GetMapping("/paymentid/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AbstractPaymentInformation getByPaymentId(@PathVariable UUID id) {
        return paymentInfoService.getByPaymentId(id);
    }

    @GetMapping("/studentid/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentsInfoSearchDto getByStudentId(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pagesize", defaultValue = "10") int pageSize,
            @RequestParam(name = "iscurrent") boolean isCurrent,
            @PathVariable UUID id) {
        return paymentInfoService.getByStudentId(page, pageSize, id, isCurrent);
    }

    @PostMapping("/studentids")
    @ResponseStatus(HttpStatus.OK)
    public Map<UUID, BigDecimal> findAllByStudentId(@RequestBody Set<UUID> studentIds) {
        return paymentInfoService.getAllByStudentId(studentIds);
    }

    @DeleteMapping("/studentid/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteByStudentId(@PathVariable UUID id) {
        paymentInfoService.deleteByStudentId(id);
    }

    @PostMapping("")
    public ResponseEntity<String> addNewPaymentToStudent(@RequestBody @Valid PaymentInfoDataDto paymentInfoData) {
        paymentInfoService.addEntity(paymentInfoData);
        return new ResponseEntity<>("Payment info created", HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePaymentInfoById(@PathVariable UUID id) {
        paymentInfoService.deleteById(id);
        return new ResponseEntity<>("Payment info deleted", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePaymentInfoById(@PathVariable UUID id, @RequestBody @Valid PaymentInfoDataDto paymentInfoData) {
        paymentInfoService.updateById(id, paymentInfoData);
        return new ResponseEntity<>("Payment info updated", HttpStatus.OK);
    }

    @PutMapping("/archive/{id}")
    public ResponseEntity<String> moveToArchiveById(@PathVariable UUID id) {
        paymentInfoService.movePaymentsToArchive(id);
        return new ResponseEntity<>("Payment moved to archive", HttpStatus.OK);
    }


}
