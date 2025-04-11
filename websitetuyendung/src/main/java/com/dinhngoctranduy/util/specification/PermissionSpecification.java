package com.dinhngoctranduy.util.specification;

import com.dinhngoctranduy.model.Permission;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class PermissionSpecification {

    public static Specification<Permission> withFilters(String name, String description, String module) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (description != null && !description.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            if (module != null && !module.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("module")), module.toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

