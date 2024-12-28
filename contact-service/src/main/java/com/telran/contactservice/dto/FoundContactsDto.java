package com.telran.contactservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoundContactsDto {
    private List<Object> foundContacts;
    private long elementsCount;
}
