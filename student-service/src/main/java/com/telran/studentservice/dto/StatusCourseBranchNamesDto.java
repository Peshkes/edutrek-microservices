package com.telran.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StatusCourseBranchNamesDto {
    private String branchName;
    private String courseName;
    private String statusName;
}
