package com.telran.contactservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindStudentsForContactsDto {
    private String search;
    private Integer statusId;
    private UUID courseId;
    private boolean isCurrentRepository;
    private Pageable pageable;
    private int offset;
}