package com.localeconnect.app.itinerary.repository;


import com.localeconnect.app.itinerary.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByItineraryId(Long itineraryId);
}
