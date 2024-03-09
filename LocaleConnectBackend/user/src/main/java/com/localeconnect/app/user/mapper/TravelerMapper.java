package com.localeconnect.app.user.mapper;

import com.localeconnect.app.user.dto.TravelerDTO;
import com.localeconnect.app.user.model.Traveler;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TravelerMapper {
    TravelerDTO toDomain(Traveler travler);

    Traveler toEntity(TravelerDTO travelerDTO);
}
