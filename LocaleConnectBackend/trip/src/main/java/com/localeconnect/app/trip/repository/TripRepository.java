package com.localeconnect.app.trip.repository;

import com.localeconnect.app.trip.model.Trip;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findByLocalguideIdAndName(Long localguideId, String name);
    Optional<List<Trip>> findByLocalguideId(Long localguideId);
    Optional<Trip> findByName(String name);
    List<Trip> findAll(Specification<Trip> specification);
}
