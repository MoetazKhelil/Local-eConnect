package com.localeconnect.app.feed.mapper;
import com.localeconnect.app.feed.dto.PostDTO;
import com.localeconnect.app.feed.model.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostDTO toDomain(Post post);
    Post toEntity(PostDTO postDTO);
}

