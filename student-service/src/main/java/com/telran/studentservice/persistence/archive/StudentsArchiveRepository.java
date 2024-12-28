package com.telran.studentservice.persistence.archive;


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
public interface StudentsArchiveRepository extends IStudentRepository<StudentsArchiveEntity>, JpaRepository<StudentsArchiveEntity, UUID>, JpaSpecificationExecutor<StudentsArchiveEntity> {

//    @Query(nativeQuery = true, value = """
//                SELECT ars.student_id, ars.contact_name, ars.phone, ars.email, ars.status_id,
//                       ars.branch_id, ars.target_course_id, ars.comment, ars.full_payment, ars.documents_done,
//                       ars.archivation_date, ars.reason_of_archivation, arsbg.group_id
//                FROM archive.students ars INNER JOIN archive.students_by_group arsbg ON ars.student_id = arsbg.student_id
//                         WHERE (:search IS NULL OR LOWER(ars.contact_name) LIKE LOWER(CONCAT('%', :search, '%'))
//                       OR LOWER(ars.phone) LIKE LOWER(CONCAT('%', :search, '%'))
//                       OR LOWER(ars.email) LIKE LOWER(CONCAT('%', :search, '%')))
//                  AND (:statusId IS NULL OR ars.status_id = :statusId)
//                  AND (:courseId IS NULL OR ars.target_course_id = :courseId)
//                  AND (:groupId IS NULL OR arsbg.group_id = :groupId)
//            """)
//    Page<StudentsArchiveEntity> findStudentsWithFilters(
//            @Param("search") String search,
//            @Param("statusId") Integer statusId,
//            @Param("courseId") UUID courseId,
//            @Param("groupId") UUID groupId,
//            Pageable pageable);

    @Query(nativeQuery = true, value = """
                SELECT ars.student_id, ars.contact_name, ars.phone, ars.email, ars.status_id,
                       ars.branch_id, ars.target_course_id, ars.comment, ars.full_payment, ars.documents_done,
                       ars.archivation_date, ars.reason_of_archivation
                FROM archive.students ars
                LEFT JOIN current.students_by_group arsbg ON ars.student_id = arsbg.student_id
                WHERE (:search IS NULL OR LOWER(ars.contact_name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(ars.phone) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(ars.email) LIKE LOWER(CONCAT('%', :search, '%')))
                  AND (:statusId IS NULL OR ars.status_id = :statusId)
                  AND (:courseId IS NULL OR ars.target_course_id = :courseId)
                  AND (:groupId IS NULL OR arsbg.group_id = :groupId)
                GROUP BY ars.student_id, ars.contact_name, ars.phone, ars.email, ars.status_id,
                         ars.branch_id, ars.target_course_id, ars.comment, ars.full_payment, ars.documents_done
            """)
    Page<StudentsArchiveEntity> findStudentsWithFilters(
            @Param("search") String search,
            @Param("statusId") Integer statusId,
            @Param("courseId") UUID courseId,
            @Param("groupId") UUID groupId,
            Pageable pageable);

}
