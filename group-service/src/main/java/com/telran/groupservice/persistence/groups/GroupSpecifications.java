package com.telran.groupservice.persistence.groups;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class GroupSpecifications {

    public static <T extends BaseGroup> Specification<T> hasCourseId(UUID courseId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("courseId"), courseId);
    }

    public static <T extends BaseGroup> Specification<T> searchByQuery(String searchQuery) {
        return (root, query, criteriaBuilder) -> {
            String likePattern = "%" + searchQuery.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("groupName")), likePattern)
            );
        };
    }
}
