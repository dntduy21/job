package com.dinhngoctranduy.util.specification;

import com.dinhngoctranduy.model.Resume;
import com.dinhngoctranduy.model.ResumeDetails;
import com.dinhngoctranduy.util.constant.ResumeState;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResumeSpecification {

    public static Specification<Resume> withAllFilters(
            String email,
            String url,
            ResumeState status,
            Boolean isParsed,
            Long userId,
            Long jobId,
            List<String> skills,
            String education,
            String address,
            Integer minYearsOfExperience,
            Integer maxYearsOfExperience,
            String certificate,
            Integer minScore,
            Integer maxScore
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ====================== Resume filters ======================
            if (email != null && !email.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            if (url != null && !url.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("url")), "%" + url.toLowerCase() + "%"));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (isParsed != null) {
                predicates.add(cb.equal(root.get("isParsed"), isParsed));
            }

            if (userId != null) {
                predicates.add(cb.equal(root.join("user").get("id"), userId));
            }

            if (jobId != null) {
                predicates.add(cb.equal(root.join("job").get("id"), jobId));
            }

            // ====================== ResumeDetails filters ======================
            Join<Resume, ResumeDetails> details = root.join("resumeDetails", JoinType.LEFT);
            List<Predicate> orPredicates = new ArrayList<>();

            if (skills != null && !skills.isEmpty()) {
                List<Predicate> skillPreds = skills.stream()
                        .map(skill -> cb.like(cb.lower(details.get("skills")), "%" + skill.toLowerCase() + "%"))
                        .collect(Collectors.toList());
                orPredicates.add(cb.or(skillPreds.toArray(new Predicate[0])));
            }

            if (education != null && !education.isEmpty()) {
                orPredicates.add(cb.like(cb.lower(details.get("education")), "%" + education.toLowerCase() + "%"));
            }

            if (address != null && !address.isEmpty()) {
                orPredicates.add(cb.like(cb.lower(details.get("address")), "%" + address.toLowerCase() + "%"));
            }

            if (minYearsOfExperience != null && maxYearsOfExperience != null) {
                orPredicates.add(cb.between(details.get("yearsOfExperience"), minYearsOfExperience, maxYearsOfExperience));
            } else if (minYearsOfExperience != null) {
                orPredicates.add(cb.greaterThanOrEqualTo(details.get("yearsOfExperience"), minYearsOfExperience));
            } else if (maxYearsOfExperience != null) {
                orPredicates.add(cb.lessThanOrEqualTo(details.get("yearsOfExperience"), maxYearsOfExperience));
            }

            if (certificate != null && !certificate.isEmpty()) {
                orPredicates.add(cb.like(cb.lower(details.get("certificates")), "%" + certificate.toLowerCase() + "%"));
            }

            if (minScore != null && maxScore != null) {
                orPredicates.add(cb.between(details.get("score"), minScore, maxScore));
            } else if (minScore != null) {
                orPredicates.add(cb.greaterThanOrEqualTo(details.get("score"), minScore));
            } else if (maxScore != null) {
                orPredicates.add(cb.lessThanOrEqualTo(details.get("score"), maxScore));
            }

            // Kết hợp: AND giữa các điều kiện ở Resume + OR các điều kiện từ ResumeDetails
            if (!orPredicates.isEmpty()) {
                predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}


