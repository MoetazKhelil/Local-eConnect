package com.localeconnect.app.itinerary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localeconnect.app.itinerary.dto.*;
import com.localeconnect.app.itinerary.service.ItineraryService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ItineraryControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Mock
    private ItineraryService itineraryService;
    @InjectMocks
    private ItineraryController itineraryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itineraryController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createItinerary_ShouldReturnSuccess() throws Exception {
        ItineraryDTO itineraryDTO = ItineraryDTO.builder()
                .userId(1L)
                .name("Holiday Trip")
                .description("A trip to remember")
                .numberOfDays(5)
                .placesToVisit(List.of("Paris", "London"))
                .build();

        given(itineraryService.createItinerary(any(ItineraryDTO.class), anyLong())).willReturn(itineraryDTO);

        mockMvc.perform(post("/api/itinerary/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itineraryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"));
    }

    @Test
    void getAllItineraries_ShouldReturnAllItineraries() throws Exception {
        List<ItineraryDTO> itineraries = List.of(
                ItineraryDTO.builder().name("Trip One").build(),
                ItineraryDTO.builder().name("Trip Two").build()
        );

        given(itineraryService.getAllItineraries()).willReturn(itineraries);

        mockMvc.perform(get("/api/itinerary/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"))
                .andExpect(jsonPath("$.data[0].name").value(itineraries.get(0).getName()))
                .andExpect(jsonPath("$.data[1].name").value(itineraries.get(1).getName()));
    }

    @Test
    void updateItinerary_ShouldReturnUpdatedItinerary() throws Exception {
        ItineraryDTO itineraryDTO = ItineraryDTO.builder()
                .name("Updated Holiday Trip")
                .description("An unforgettable trip")
                .numberOfDays(7)
                .placesToVisit(List.of("New Destination"))
                .build();

        given(itineraryService.updateItinerary(any(ItineraryDTO.class), anyLong())).willReturn(itineraryDTO);

        mockMvc.perform(put("/api/itinerary/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itineraryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"))
                .andExpect(jsonPath("$.data.name").value("Updated Holiday Trip"));
    }


    @Test
    void deleteItinerary_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/itinerary/delete/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"));
    }

}
