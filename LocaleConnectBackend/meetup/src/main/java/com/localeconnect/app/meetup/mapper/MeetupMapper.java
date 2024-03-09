package com.localeconnect.app.meetup.mapper;

import com.localeconnect.app.meetup.dto.MeetupDTO;
import com.localeconnect.app.meetup.model.Meetup;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;


@Mapper(componentModel = "spring")
public interface MeetupMapper {

    MeetupDTO toDomain(Meetup meetup);
    Meetup toEntity(MeetupDTO meetupDTO);
}
