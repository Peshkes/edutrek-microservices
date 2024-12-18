package com.telran.contactservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindStudentsForContactsDto {
    private int quantity;
    private String search;
    private Integer statusId;
    private UUID group_id;
    private UUID courseId;
    private boolean isCurrentRepository;

}
