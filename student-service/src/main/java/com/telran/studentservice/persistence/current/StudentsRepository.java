package com.telran.studentservice.persistence.current;


import com.telran.studentservice.persistence.IStudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentsRepository extends IStudentRepository<StudentEntity>, JpaRepository<StudentEntity, UUID>, JpaSpecificationExecutor<StudentEntity> {
    StudentEntity findByPhoneOrEmail(String phone, String email);

    @Query(nativeQuery = true, value = """
                            SELECT сs.student_id, сs.contact_name, сs.phone, сs.email, сs.status_id,
                       сs.branch_id, сs.target_course_id, сs.comment, сs.full_payment, сs.documents_done
                FROM current.students сs
                LEFT JOIN current.students_by_group sbg ON сs.student_id = sbg.student_id
                WHERE (:search IS NULL OR LOWER(сs.contact_name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(сs.phone) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(сs.email) LIKE LOWER(CONCAT('%', :search, '%')))
                  AND (:statusId IS NULL OR сs.status_id = :statusId)
                  AND (:courseId IS NULL OR сs.target_course_id = :courseId)
                  AND (:groupId IS NULL OR sbg.group_id = :groupId)
                GROUP BY сs.student_id, сs.contact_name, сs.phone, сs.email, сs.status_id,
                         сs.branch_id, сs.target_course_id, сs.comment, сs.full_payment, сs.documents_done
            """)
    Page<StudentEntity> findStudentsWithFilters(
            @Param("search") String search,
            @Param("statusId") Integer statusId,
            @Param("courseId") UUID courseId,
            @Param("groupId") UUID groupId,
            Pageable pageable);




}
