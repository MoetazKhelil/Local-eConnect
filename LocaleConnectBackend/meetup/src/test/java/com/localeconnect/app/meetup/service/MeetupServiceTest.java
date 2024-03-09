package com.localeconnect.app.meetup.service;

import com.localeconnect.app.meetup.dto.MeetupDTO;
import com.localeconnect.app.meetup.dto.MeetupEditDTO;
import com.localeconnect.app.meetup.mapper.MeetupMapper;
import com.localeconnect.app.meetup.model.Meetup;
import com.localeconnect.app.meetup.repository.MeetupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MeetupServiceTest {
    @InjectMocks
    private MeetupService meetupService;

    @Mock
    private MeetupRepository meetupRepository;

    @Mock
    private MeetupMapper meetupMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(meetupMapper.toEntity(any(MeetupDTO.class))).thenAnswer(invocation -> {
            MeetupDTO dto = invocation.getArgument(0);
            Meetup entity = new Meetup();
            entity.setName(dto.getName());
            return entity;
        });

        when(meetupMapper.toDomain(any(Meetup.class))).thenAnswer(invocation -> {
            Meetup entity = invocation.getArgument(0);
            MeetupDTO dto = new MeetupDTO();
            dto.setName(entity.getName());
            return dto;
        });
    }

    @Test
    void testGetAllMeetups() {
        List<Meetup> meetups = new ArrayList<>();
        Meetup meetup1 = new Meetup();
        meetup1.setName("Meetup 1");
        Meetup meetup2 = new Meetup();
        meetup2.setName("Meetup 2");
        meetups.add(meetup1);
        meetups.add(meetup2);

        when(meetupRepository.findAll()).thenReturn(meetups);

        List<MeetupDTO> result = meetupService.getAllMeetups();

        assertNotNull(result);
        assertEquals(meetups.size(), result.size());
        assertEquals(meetups.get(0).getName(), result.get(0).getName());
        assertEquals(meetups.get(1).getName(), result.get(1).getName());
    }

    @Test
    void testGetById() {
        Long meetupId = 1L;
        Meetup meetup = new Meetup();
        meetup.setId(meetupId);
        meetup.setName("Meetup 1");
        when(meetupRepository.findById(meetupId)).thenReturn(Optional.of(meetup));
        MeetupDTO expectedDTO = new MeetupDTO();
        expectedDTO.setName("Meetup 1");
        when(meetupMapper.toDomain(meetup)).thenReturn(expectedDTO);

        MeetupDTO result = meetupService.getMeetupById(meetupId);

        assertNotNull(result);
        assertEquals("Meetup 1", result.getName());
    }

    @Test
    void testUpdateMeetupSuccess() {
        Long meetupId = 1L;
        MeetupEditDTO meetupEditDTO = new MeetupEditDTO();
        meetupEditDTO.setName("Updated Adventure Meetup");
        Meetup existingMeetup = new Meetup();
        existingMeetup.setId(meetupId);
        existingMeetup.setName("Original Adventure Meetup");

        when(meetupRepository.findById(meetupId)).thenReturn(Optional.of(existingMeetup));

        when(meetupRepository.save(any(Meetup.class))).thenReturn(existingMeetup);

        MeetupDTO expectedDTO = new MeetupDTO();
        expectedDTO.setName(meetupEditDTO.getName());
        when(meetupMapper.toDomain(any(Meetup.class))).thenReturn(expectedDTO);

        MeetupDTO updatedMeetup = meetupService.updateMeetup(meetupEditDTO,meetupId);

        assertNotNull(updatedMeetup, "The updated meetup should not be null.");
        assertEquals(meetupEditDTO.getName(), updatedMeetup.getName(), "The names should match after the update.");
        verify(meetupRepository, times(1)).save(existingMeetup);
    }

    @Test
    void testDeleteMeetupSuccess() {
        Long meetupId = 1L;
        Meetup existingMeetup = new Meetup();
        existingMeetup.setId(meetupId);
        when(meetupRepository.findById(meetupId)).thenReturn(Optional.of(existingMeetup));

        meetupService.deleteMeetupById(meetupId);

        verify(meetupRepository, times(1)).deleteById(meetupId);
    }

    @Test
    void testSearchMeetupByNameSuccess() {
        String searchPattern = "%Adventure Meetup%";
        List<Meetup> foundMeetups = new ArrayList<>();
        Meetup meetup1 = new Meetup();
        meetup1.setName("Adventure Meetup 1");
        foundMeetups.add(meetup1);
        Meetup meetup2 = new Meetup();
        meetup2.setName("Adventure Meetup 2");
        foundMeetups.add(meetup2);

        when(meetupRepository.findAllByNameIgnoreCaseLike(searchPattern)).thenReturn(Optional.of(foundMeetups));

        List<MeetupDTO> expectedDTOs = foundMeetups.stream()
                .map(meetup -> {
                    MeetupDTO dto = new MeetupDTO();
                    dto.setName(meetup.getName());
                    return dto;
                }).collect(Collectors.toList());
        when(meetupMapper.toDomain(any(Meetup.class))).thenAnswer(invocation -> {
            Meetup entity = invocation.getArgument(0);
            MeetupDTO dto = new MeetupDTO();
            dto.setName(entity.getName());
            return dto;
        });

        List<MeetupDTO> result = meetupService.searchByName(searchPattern);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedDTOs.size(), result.size());
        assertEquals(expectedDTOs.get(0).getName(), result.get(0).getName());
        verify(meetupRepository, times(1)).findAllByNameIgnoreCaseLike(searchPattern);
    }

}
