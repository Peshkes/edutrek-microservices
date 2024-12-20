package com.telran.groupservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetStudentsByGroupDto {
    private UUID groupId;
    private UUID studentId;
    private Boolean isActive;
    private String groupName;

}
