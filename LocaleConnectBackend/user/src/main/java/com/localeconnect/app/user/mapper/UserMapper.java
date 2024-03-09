package com.localeconnect.app.user.mapper;

import com.localeconnect.app.user.dto.UserDTO;
import com.localeconnect.app.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDomain(User user);
    User toEntity(UserDTO userDTO);
    void updateUserFromDto(UserDTO dto, @MappingTarget User entity);
}
