package com.telran.groupservice.dto;

import com.telran.groupservice.persistence.groups.BaseGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationGroupResponseDto {
    private List<? extends BaseGroup> groups;
    private long total;
    private int page;
    private int size;
}
