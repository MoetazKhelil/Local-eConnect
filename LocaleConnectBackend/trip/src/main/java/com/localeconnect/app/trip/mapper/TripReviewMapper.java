package com.localeconnect.app.trip.mapper;

import com.localeconnect.app.trip.dto.TripReviewDTO;
import com.localeconnect.app.trip.model.TripReview;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TripReviewMapper {
    TripReview toEntity(TripReviewDTO tripReviewDTO);
    TripReviewDTO toDomain(TripReview tripReview);
    void updateTripReviewFromDto(TripReviewDTO tripReviewDTO, @MappingTarget TripReview entity);
}
