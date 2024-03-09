package com.localeconnect.app.itinerary.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "itinerary_id", nullable = false)
    private Long itineraryId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 1000)
    private String text;

    private LocalDateTime timestamp;


}
