package com.telran.studentservice.persistence.current;


import com.telran.studentservice.persistence.IStudentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentsRepository extends IStudentRepository<StudentEntity>,JpaRepository<StudentEntity, UUID> , JpaSpecificationExecutor<StudentEntity>{
    StudentEntity findByPhoneOrEmail(String phone, String email);
}
