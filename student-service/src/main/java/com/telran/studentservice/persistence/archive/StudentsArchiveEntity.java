package com.telran.studentservice.persistence.archive;


import com.telran.studentservice.persistence.AbstractStudent;
import com.telran.studentservice.persistence.current.StudentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.time.LocalDate;



@NoArgsConstructor
@Entity
@Table(schema = "archive", name = "students")
public class StudentsArchiveEntity extends AbstractStudent {

        @Column(name = "archivation_date")
        private LocalDate dateOfArchivation;
        @Column(name = "reason_of_archivation")
        private String reasonOfArchivation;


        public StudentsArchiveEntity(StudentEntity studentEntity, String reasonOfArchivation) {
                super(studentEntity);
                this.dateOfArchivation = LocalDate.now();
                this.reasonOfArchivation = reasonOfArchivation;
        }
}


