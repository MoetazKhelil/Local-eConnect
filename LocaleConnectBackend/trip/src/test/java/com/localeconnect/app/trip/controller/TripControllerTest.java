package com.localeconnect.app.trip.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localeconnect.app.trip.dto.TripDTO;
import com.localeconnect.app.trip.service.TripService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class TripControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Mock
    private TripService tripService;

    @InjectMocks
    private TripController tripController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tripController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper().registerModule(new JavaTimeModule())))
                .build();
    }

    @Test
    void createTrip_ShouldReturnSuccess() throws Exception {

        TripDTO tripDTO = TripDTO.builder()
                .localguideId(1L)
                .name("Test Trip")
                .description("A test trip description")
                .destination("Test Destination")
                .durationInHours(48)
                .capacity(5)
                .languages(List.of("English", "Spanish"))
                .dailyActivities(List.of("Activity 1", "Activity 2"))
                .travelers(List.of(1L))
                .build();

        given(tripService.createTrip(any(TripDTO.class))).willReturn(tripDTO);

        mockMvc.perform(post("/api/trip/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"));
    }

    @Test
    void getAllTrips_ShouldReturnAllTrips() throws Exception {
        List<TripDTO> trips = List.of(
                TripDTO.builder().name("Trip One").destination("Destination One").build(),
                TripDTO.builder().name("Trip Two").destination("Destination Two").build()
        );
        given(tripService.getAllTrips()).willReturn(trips);

        mockMvc.perform(get("/api/trip/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"))
                .andExpect(jsonPath("$.data[0].name").value(trips.get(0).getName()))
                .andExpect(jsonPath("$.data[1].name").value(trips.get(1).getName()));
    }

    @Test
    void getTripById_ShouldReturnTrip() throws Exception {
        TripDTO trip = TripDTO.builder().name("Trip One").destination("Destination One").build();
        given(tripService.getById(anyLong())).willReturn(trip);

        mockMvc.perform(get("/api/trip/{trip_id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"))
                .andExpect(jsonPath("$.data.name").value(trip.getName()));
    }

    @Test
    void updateTrip_ShouldReturnUpdatedTrip() throws Exception {
        TripDTO tripDTO = TripDTO.builder()
                .name("Updated Trip")
                .destination("Updated Destination")
                .build();
        given(tripService.updateTrip(anyLong(), any(TripDTO.class))).willReturn(tripDTO);

        mockMvc.perform(put("/api/trip/update/{trip_id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"))
                .andExpect(jsonPath("$.data.name").value("Updated Trip"))
                .andExpect(jsonPath("$.data.destination").value("Updated Destination"));
    }

    @Test
    void deleteTrip_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/trip/delete/{trip_id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"));
    }
}