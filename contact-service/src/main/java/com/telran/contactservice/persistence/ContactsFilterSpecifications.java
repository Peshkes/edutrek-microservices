package com.telran.contactservice.persistence;


import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

@NoArgsConstructor
public class ContactsFilterSpecifications<E extends AbstractContacts> {


    public Specification<E> hasName(String name) {
        return (r, q, cb) -> cb.like(r.get("contactName"), "%" + name + "%");
    }

    public Specification<E> hasPhone(String phone) {
        return (r, q, cb) -> cb.like(r.get("phone"), "%" + phone + "%");
    }

    public Specification<E> hasEmail(String email) {
        return (r, q, cb) -> cb.like(r.get("email"), "%" + email + "%");
    }


    public Specification<E> hasTargetCourseId(UUID targetCourseId) {
        return (r, q, cb) -> cb.equal(r.get("targetCourseId"), targetCourseId);
    }


    public Specification<E> hasStatusId(int status_id) {
        return (r, q, cb) -> cb.equal(r.get("statusId"), status_id);
    }


    public Specification<E> getSpecifications(String search, Integer statusId, UUID targetCourseId) {
        Specification<E> specs = Specification.where(null);
        if (statusId != null)
            specs = specs.and(this.hasStatusId(statusId));
        if (targetCourseId != null)
            specs = specs.and(this.hasTargetCourseId(targetCourseId));
        if (search != null && !search.isEmpty() && !search.isBlank()) {
            specs = specs.and(this.hasName(search))
                    .or(this.hasPhone(search))
                    .or(this.hasEmail(search));
        }
        return specs;
    }


}
