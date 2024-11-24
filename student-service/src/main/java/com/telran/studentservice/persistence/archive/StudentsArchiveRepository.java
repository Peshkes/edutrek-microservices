package com.telran.studentservice.persistence.archive;


import com.telran.studentservice.persistence.IStudentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentsArchiveRepository extends IStudentRepository<StudentsArchiveEntity>, JpaRepository<StudentsArchiveEntity, UUID>, JpaSpecificationExecutor<StudentsArchiveEntity> {

}
