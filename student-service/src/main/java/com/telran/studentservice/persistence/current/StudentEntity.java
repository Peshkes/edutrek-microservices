package com.telran.studentservice.persistence.current;


import com.telran.studentservice.dto.StudentsDataDto;
import com.telran.studentservice.dto.StudentsFromContactDataDto;
import com.telran.studentservice.persistence.AbstractStudent;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(schema = "current", name = "students")
public class StudentEntity extends AbstractStudent {

    public StudentEntity(StudentsDataDto studentsDataDto, int statusId) {
        super(studentsDataDto,statusId);
    }

    public StudentEntity(AbstractContacts contactData, StudentsFromContactDataDto studentsDataDto) {
        super(contactData, studentsDataDto);
    }
}
