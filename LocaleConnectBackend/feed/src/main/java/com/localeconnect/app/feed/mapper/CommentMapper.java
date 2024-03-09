package com.localeconnect.app.feed.mapper;

import com.localeconnect.app.feed.dto.CommentDTO;
import com.localeconnect.app.feed.model.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDTO toDomain(Comment comment);
    Comment toEntity(CommentDTO commentDTO);
}
