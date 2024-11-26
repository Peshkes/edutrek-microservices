package com.telran.studentservice.dto;

import com.telran.studentservice.persistence.AbstractStudent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
public class FoundEntitiesDto {
    private List<AbstractStudent> foundStudents;

    public List<AbstractStudent> getFoundStudents() {
        return new ArrayList<>(foundStudents);
    }

}
