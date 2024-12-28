package com.telran.studentservice.dto;

import com.telran.studentservice.persistence.AbstractStudent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentServiceNestedObject {
    private List<AbstractStudent> foundStudents;
    private Page<? extends AbstractStudent> studentsPaged;
    private long elementsCount;

    public StudentServiceNestedObject(Page<? extends AbstractStudent> studentsPaged) {
        this.studentsPaged = studentsPaged;
        this.foundStudents = new ArrayList<>(studentsPaged.getContent());
        this.elementsCount = studentsPaged.getTotalElements();
    }
}
