package com.localeconnect.app.feed.mapper;

import com.localeconnect.app.feed.dto.LikeDTO;
import com.localeconnect.app.feed.model.Like;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    LikeDTO toDomain(Like like);
    Like toEntity(LikeDTO likeDTO);

}
