package com.localeconnect.app.meetup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localeconnect.app.meetup.dto.MeetupDTO;
import com.localeconnect.app.meetup.service.MeetupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class MeetupControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private MeetupService meetupService;

    @InjectMocks
    private MeetupController meetupController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(meetupController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper().registerModule(new JavaTimeModule())))
                .build();
    }

    @Test
    void createMeetup_ShouldReturnSuccess() throws Exception {
        MeetupDTO meetupDTO = new MeetupDTO();
        meetupDTO.setCreatorId(1L);
        meetupDTO.setName("Test Meetup");
        meetupDTO.setDescription("A test meetup description");
        meetupDTO.setDate(new Date());
        meetupDTO.setStartTime("10:00");
        meetupDTO.setEndTime("12:00");
        meetupDTO.setCost(0.0);
        meetupDTO.setLocation("Test Location");
        meetupDTO.setSpokenLanguages(List.of("English"));

        given(meetupService.createMeetup(any(MeetupDTO.class))).willReturn(meetupDTO);

        mockMvc.perform(post("/api/meetup/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetupDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Success!"));
    }

    @Test
    void getAllMeetups_ShouldReturnAllMeetups() throws Exception {
        // DTO
        MeetupDTO meetupOne = new MeetupDTO();
        meetupOne.setId(1L);
        meetupOne.setCreatorId(1L);
        meetupOne.setName("Meetup One");
        meetupOne.setDescription("Description One");
        meetupOne.setDate(new Date());
        meetupOne.setStartTime("10:00");
        meetupOne.setEndTime("12:00");
        meetupOne.setCost(0.0);
        meetupOne.setLocation("Location One");
        meetupOne.setSpokenLanguages(List.of("English"));

        //Entity
        MeetupDTO meetupTwo = new MeetupDTO();
        meetupTwo.setId(2L);
        meetupTwo.setCreatorId(2L);
        meetupTwo.setName("Meetup Two");
        meetupTwo.setDescription("Description Two");
        meetupTwo.setDate(new Date());
        meetupTwo.setStartTime("13:00");
        meetupTwo.setEndTime("15:00");
        meetupTwo.setCost(0.0);
        meetupTwo.setLocation("Location Two");
        meetupTwo.setSpokenLanguages(List.of("Spanish"));

        List<MeetupDTO> meetups = List.of(meetupOne, meetupTwo);
        given(meetupService.getAllMeetups()).willReturn(meetups);

        mockMvc.perform(get("/api/meetup/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"))
                .andExpect(jsonPath("$.data[0].name").value(meetups.get(0).getName()))
                .andExpect(jsonPath("$.data[1].name").value(meetups.get(1).getName()));
    }

    @Test
    void getMeetupById_ShouldReturnMeetup() throws Exception {
        MeetupDTO meetup = new MeetupDTO();
        meetup.setId(1L);
        meetup.setCreatorId(1L);
        meetup.setName("Meetup One");
        meetup.setDescription("Description One");
        meetup.setDate(new Date());
        meetup.setStartTime("10:00");
        meetup.setEndTime("12:00");
        meetup.setCost(0.0);
        meetup.setLocation("Location One");
        meetup.setSpokenLanguages(List.of("English"));

        given(meetupService.getMeetupById(anyLong())).willReturn(meetup);

        mockMvc.perform(get("/api/meetup/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"))
                .andExpect(jsonPath("$.data.name").value(meetup.getName()));
    }


}

