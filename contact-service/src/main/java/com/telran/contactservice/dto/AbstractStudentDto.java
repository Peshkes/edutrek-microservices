package com.telran.contactservice.dto;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractStudentDto {
    private UUID studentId;
    private String contactName;
    private String phone;
    private String email;
    private int statusId;
    private int branchId;
    private UUID targetCourseId;
    private String comment;
    private int fullPayment;
    private boolean documentsDone;
    
}