package com.telran.studentservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class StudentsFromContactDataDto {
    UUID contactId;
    private int fullPayment;
    private boolean documentsDone;
}
