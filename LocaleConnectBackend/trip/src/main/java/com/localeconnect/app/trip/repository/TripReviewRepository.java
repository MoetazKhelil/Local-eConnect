package com.localeconnect.app.trip.repository;

import com.localeconnect.app.trip.model.TripReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripReviewRepository extends JpaRepository<TripReview, Long> {
     List<TripReview> findByTripId(Long tripId);
}
