package com.telran.studentservice.dto;

import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class AbstractContactsDto {
    private UUID contactId;
    private String contactName;
    private String phone;
    private String email;
    private int statusId;
    private int branchId;
    private UUID targetCourseId;
    private String comment;
}
