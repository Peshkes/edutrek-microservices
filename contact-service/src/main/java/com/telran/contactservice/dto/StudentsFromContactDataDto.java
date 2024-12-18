package com.telran.contactservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentsFromContactDataDto {
    UUID contactId;
    private int fullPayment;
    private boolean documentsDone;
}
