package com.localeconnect.app.itinerary.repository;

import com.localeconnect.app.itinerary.model.Itinerary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    Optional<List<Itinerary>> findByUserId(Long userId);

    Optional<List<Itinerary>> findAllIByNameIgnoreCaseLike(String name);

    Optional<List<Itinerary>> findAll(Specification<Itinerary> specification);

    Boolean existsByUserIdAndName(Long userId, String name);

}

