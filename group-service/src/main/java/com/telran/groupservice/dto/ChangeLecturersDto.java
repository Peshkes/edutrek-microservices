package com.telran.groupservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeLecturersDto {
    private UUID lecturerId;
    private boolean isWebinarist;
    public boolean getIsWebinarist() {
        return isWebinarist;
    }
}
