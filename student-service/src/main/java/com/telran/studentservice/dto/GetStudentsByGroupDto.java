package com.telran.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetStudentsByGroupDto {
    private UUID studentId;
    private UUID groupId;
    private String groupName;
    private Boolean isActive;
}
