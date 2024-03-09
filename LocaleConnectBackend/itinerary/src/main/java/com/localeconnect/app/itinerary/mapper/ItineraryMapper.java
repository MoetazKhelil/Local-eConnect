package com.localeconnect.app.itinerary.mapper;

import com.localeconnect.app.itinerary.dto.ItineraryDTO;
import com.localeconnect.app.itinerary.model.Itinerary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItineraryMapper {
    ItineraryDTO toDomain(Itinerary itinerary);
    Itinerary toEntity(ItineraryDTO itineraryDTO);
}
