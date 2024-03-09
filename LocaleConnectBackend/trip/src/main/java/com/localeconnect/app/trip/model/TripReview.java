package com.localeconnect.app.trip.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripReviewId;
    @Column(name = "trip_id", nullable = false)
    private Long tripId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(length = 1000)
    private String text;

    private LocalDateTime timestamp;

}
