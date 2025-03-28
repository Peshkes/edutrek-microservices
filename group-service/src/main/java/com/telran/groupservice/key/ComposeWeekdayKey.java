package com.telran.groupservice.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComposeWeekdayKey implements Serializable {
    private UUID groupId;
    private int weekdayId;
}