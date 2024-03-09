package com.localeconnect.app.trip.service;

import com.localeconnect.app.trip.dto.TripDTO;
import com.localeconnect.app.trip.mapper.TripMapper;
import com.localeconnect.app.trip.model.Trip;
import com.localeconnect.app.trip.repository.TripRepository;
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

import static org.mockito.Mockito.when;

public class TripServiceTest {
    @InjectMocks
    private TripService tripService;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private TripMapper tripMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(tripMapper.toEntity(any(TripDTO.class))).thenAnswer(invocation -> {
            TripDTO dto = invocation.getArgument(0);
            Trip entity = new Trip();
            entity.setName(dto.getName());
            entity.setLanguages(dto.getLanguages());
            entity.setDestination(dto.getDestination());
            entity.setDurationInHours(dto.getDurationInHours());
            entity.setTravelers(dto.getTravelers());
            return entity;
        });

        when(tripMapper.toDomain(any(Trip.class))).thenAnswer(invocation -> {
            Trip entity = invocation.getArgument(0);
            TripDTO dto = new TripDTO();
            dto.setName(entity.getName());
            dto.setDestination(entity.getDestination());
            dto.setTravelers(entity.getTravelers());
            dto.setDurationInHours(entity.getDurationInHours());
            dto.setLanguages(dto.getLanguages());
            return dto;
        });
    }

    @Test
    void testGetAllTrips() {
        List<Trip> trips = new ArrayList<>();
        Trip trip1 = new Trip();
        trip1.setName("Trip 1");
        Trip trip2 = new Trip();
        trip2.setName("Trip 2");
        trips.add(trip1);
        trips.add(trip2);

        when(tripRepository.findAll()).thenReturn(trips);

        List<TripDTO> result = tripService.getAllTrips();

        assertNotNull(result);
        assertEquals(trips.size(), result.size());
        assertEquals(trips.get(0).getName(), result.get(0).getName());
        assertEquals(trips.get(1).getName(), result.get(1).getName());
    }

    @Test
    void testGetById() {
        Long tripId = 1L;
        Trip trip = new Trip();
        trip.setId(tripId);
        trip.setName("Trip 1");
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        TripDTO expectedDTO = new TripDTO();
        expectedDTO.setName("Trip 1");
        when(tripMapper.toDomain(trip)).thenReturn(expectedDTO);

        TripDTO result = tripService.getById(tripId);

        assertNotNull(result);
        assertEquals("Trip 1", result.getName());
    }

    @Test
    void testUpdateTripSuccess() {
        Long tripId = 1L;
        TripDTO tripDTO = new TripDTO();
        tripDTO.setName("Updated Adventure Trip");
        Trip existingTrip = new Trip();
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(existingTrip));
        when(tripRepository.save(any(Trip.class))).thenReturn(existingTrip);
        when(tripMapper.toDomain(any(Trip.class))).thenReturn(tripDTO);

        TripDTO updatedTrip = tripService.updateTrip(tripId, tripDTO);

        assertNotNull(updatedTrip);
        assertEquals(tripDTO.getName(), updatedTrip.getName());
        verify(tripRepository, times(1)).save(existingTrip);
    }

    @Test
    void testDeleteTripSuccess() {
        Long tripId = 1L;
        Trip existingTrip = new Trip();
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(existingTrip));

        tripService.deleteTrip(tripId);

        verify(tripRepository, times(1)).delete(existingTrip);
    }

    @Test
    void testSearchTripSuccess() {
        String tripName = "Adventure Trip";
        Trip trip = new Trip();
        trip.setName(tripName);
        when(tripRepository.findByName(tripName)).thenReturn(Optional.of(trip));

        TripDTO expectedTripDTO = new TripDTO();
        expectedTripDTO.setName(tripName);
        when(tripMapper.toDomain(trip)).thenReturn(expectedTripDTO);

        TripDTO result = tripService.searchTrip(tripName);

        assertNotNull(result);
        assertEquals(expectedTripDTO.getName(), result.getName());
        verify(tripRepository, times(1)).findByName(tripName);
    }
    @Test
    void testFilterSuccess() {
        String destination = "Paris";
        Double traveltime = 5.0;
        List<String> languages = List.of("French", "English");
        List<Trip> trips = new ArrayList<>();
        trips.add(new Trip());
        trips.add(new Trip());

        when(tripRepository.findAll(any(Specification.class))).thenReturn(trips);

        List<TripDTO> expectedDTOs = trips.stream()
                .map(trip -> new TripDTO())
                .collect(Collectors.toList());
        when(tripMapper.toDomain(any(Trip.class))).thenReturn(new TripDTO());

        List<TripDTO> result = tripService.filter(destination, traveltime, languages);

        assertNotNull(result);
        assertEquals(expectedDTOs.size(), result.size());
        verify(tripRepository, times(1)).findAll(any(Specification.class));
    }


}
