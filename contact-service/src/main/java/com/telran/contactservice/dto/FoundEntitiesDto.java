package com.telran.contactservice.dto;

import com.telran.contactservice.persistence.AbstractContacts;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
public class FoundEntitiesDto {
    private List<AbstractContacts> foundContacts;
    private List<AbstractStudentDto> foundStudents;

    public List<AbstractContacts> getFoundContacts() {
        return new ArrayList<>(foundContacts);
    }

    public List<AbstractStudentDto> getFoundStudents() {
        return new ArrayList<>(foundStudents);
    }


}
