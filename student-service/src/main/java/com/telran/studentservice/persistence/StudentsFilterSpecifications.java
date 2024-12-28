package com.telran.studentservice.persistence;


import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class StudentsFilterSpecifications  {

    public static <E extends AbstractStudent> Specification<E> hasName(String name) {
        return (r, q, cb) -> cb.like(r.get("contactName"), "%" + name + "%");
    }

    public static  <E extends AbstractStudent> Specification<E> hasPhone(String phone) {
        return (r, q, cb) -> cb.like(r.get("phone"), "%" + phone + "%");
    }

    public static <E extends AbstractStudent> Specification<E> hasEmail(String email) {
        return (r, q, cb) -> cb.like(r.get("email"), "%" + email + "%");   }


    public static <E extends AbstractStudent>  Specification<E> hasStatusId(int status_id) {
        return (r, q, cb) -> cb.equal(r.get("statusId"), status_id);
    }

    public static <E extends AbstractStudent> Specification<E> hasTargetCourseId(UUID targetCourseId) {
        return (r, q, cb) -> cb.equal(r.get("targetCourseId"), targetCourseId);
    }

    public static <E extends AbstractStudent> Specification<E> getStudentSpecifications( String search, Integer statusId, UUID targetCourseId) {
        Specification<E> specs = Specification.where(null);
        if (statusId != null)
            specs = specs.and(hasStatusId(statusId));
        if (targetCourseId != null)
            specs = specs.and(hasTargetCourseId(targetCourseId));
        if (search != null && !search.isEmpty() && !search.isBlank()) {
            specs = specs.and(hasName(search))
                    .or(hasPhone(search))
                    .or(hasEmail(search));
        }
        return specs;
    }


}
