package com.telran.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupsDto {
    private UUID groupId;
    private Boolean isActive;
    private String groupName;
}
