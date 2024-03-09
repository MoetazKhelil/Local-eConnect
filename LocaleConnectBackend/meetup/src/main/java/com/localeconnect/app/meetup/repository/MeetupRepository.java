package com.localeconnect.app.meetup.repository;

import com.localeconnect.app.meetup.model.Meetup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetupRepository extends JpaRepository<Meetup, Long> {

    Optional<List<Meetup>> findByCreatorId(Long userId);
    Optional<List<Meetup>> findAllByNameIgnoreCaseLike(String name);
}
