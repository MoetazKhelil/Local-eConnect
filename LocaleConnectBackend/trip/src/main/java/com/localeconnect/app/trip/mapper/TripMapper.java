package com.localeconnect.app.trip.mapper;

import com.localeconnect.app.trip.dto.TripDTO;
import com.localeconnect.app.trip.model.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TripMapper {
    Trip toEntity(TripDTO tripDTO);
    TripDTO toDomain(Trip trip);
    void updateTripFromDto(TripDTO tripDTO, @MappingTarget Trip entity);
}
