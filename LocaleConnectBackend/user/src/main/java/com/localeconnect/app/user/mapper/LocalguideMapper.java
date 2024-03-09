package com.localeconnect.app.user.mapper;

import com.localeconnect.app.user.dto.LocalguideDTO;
import com.localeconnect.app.user.model.Localguide;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocalguideMapper {
    LocalguideDTO toDomain(Localguide localguide);

    Localguide toEntity(LocalguideDTO localguideDTO);
}
