package com.dinhngoctranduy.util.specification;

import com.dinhngoctranduy.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> withFilters(
            String name,
            String email,
            Integer age,
            String gender,
            String address,
            Long roleId
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (email != null && !email.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            if (age != null) {
                predicates.add(cb.equal(root.get("age"), age));
            }

            if (gender != null && !gender.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("gender")), gender.toLowerCase()));
            }

            if (address != null && !address.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + address.toLowerCase() + "%"));
            }

            if (roleId != null) {
                predicates.add(cb.equal(root.join("role").get("id"), roleId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

