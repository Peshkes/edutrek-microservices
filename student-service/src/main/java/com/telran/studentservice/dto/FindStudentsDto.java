package com.telran.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindStudentsDto {
    private Pageable pageable;
    private String search;
    private Integer statusId;
    private UUID group_id;
    private UUID courseId;
    private boolean isCurrentRepository;

}
