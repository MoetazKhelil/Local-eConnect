package com.localeconnect.app.itinerary.repository;

import com.localeconnect.app.itinerary.dto.Tag;
import com.localeconnect.app.itinerary.model.Itinerary;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

    // TODO: add filters to frontend according to these specifications
public class ItinerarySpecification {

    public static Specification<Itinerary> hasPlace(String place) {
        return (root, query, criteriaBuilder) -> {
            if (place == null || place.isEmpty()) {
                return criteriaBuilder.disjunction();
            }
            Join<Itinerary, String> tagsJoin = root.join("placesToVisit");
            return criteriaBuilder.equal(tagsJoin, place);
        };
    }

    public static Specification<Itinerary> hasTag(Tag tag) {
        return (root, query, criteriaBuilder) -> {
            if (tag == null) {
                return criteriaBuilder.disjunction();
            }
            Join<Itinerary, Tag> tagsJoin = root.join("tags");
            return criteriaBuilder.equal(tagsJoin, tag);
        };
    }

    public static Specification<Itinerary> maxNumberOfDays(Integer days) {
        return (root, query, criteriaBuilder) -> {
            if (days == null || days < 1) {
                return criteriaBuilder.disjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("numberOfDays"), days);
        };
    }

}

