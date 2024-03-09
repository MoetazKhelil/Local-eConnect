package com.localeconnect.app.itinerary.mapper;

import com.localeconnect.app.itinerary.dto.ReviewDTO;
import com.localeconnect.app.itinerary.model.Review;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review toEntity(ReviewDTO dto);

    ReviewDTO toDomain(Review review);
}
