package com.localeconnect.app.trip.repository;

import com.localeconnect.app.trip.model.Trip;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TripSpecification {
    public static Specification<Trip> getDestination(String destination) {
        return (root, query, criteriaBuilder) -> {
            if (destination == null || destination.isEmpty()) {
                return criteriaBuilder.disjunction();
            }
            Join<Trip, String> tagsJoin = root.join("destination");
            return criteriaBuilder.equal(tagsJoin, destination);
        };
    }

    public static Specification<Trip> hasLanguages(List<String> languages) {
        return (root, query, criteriaBuilder) -> {
            if (languages == null || languages.isEmpty()) {
                return criteriaBuilder.disjunction();
            }
            Expression<List<String>> tripLanguages = root.get("languages");
            List<Predicate> languagePredicates = new ArrayList<>();
            for (String lang : languages) {
                languagePredicates.add(criteriaBuilder.isMember(lang, tripLanguages));
            }
            return criteriaBuilder.and(languagePredicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Trip> maxDuration(Double duration) {
        return (root, query, criteriaBuilder) -> {
            if (duration == null || duration <= 0) {
                return criteriaBuilder.disjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("traveltime"), duration);
        };
    }

}

