package com.telran.groupservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private UUID courseId;
    private String courseName;
    private String courseAbbreviation;
}
