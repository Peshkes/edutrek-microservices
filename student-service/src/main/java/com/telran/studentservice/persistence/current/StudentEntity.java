package com.telran.studentservice.persistence.current;


import com.telran.studentservice.dto.StudentsAddDataDto;
import com.telran.studentservice.dto.StudentsPromoteDataDto;
import com.telran.studentservice.dto.StudentsUpdateDataDto;
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

    public StudentEntity(StudentsAddDataDto studentsDataDto) {
        super(studentsDataDto);
    }

    public StudentEntity(StudentsPromoteDataDto studentsDataDto) {
        super(studentsDataDto);
    }

    public StudentEntity(StudentsUpdateDataDto studentsDataDto) {
        super(studentsDataDto);
    }
}
