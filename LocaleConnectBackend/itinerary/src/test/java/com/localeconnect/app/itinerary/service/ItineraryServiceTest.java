package com.localeconnect.app.itinerary.service;

import com.localeconnect.app.itinerary.dto.ItineraryDTO;
import com.localeconnect.app.itinerary.mapper.ItineraryMapper;
import com.localeconnect.app.itinerary.model.Itinerary;
import com.localeconnect.app.itinerary.repository.ItineraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ItineraryServiceTest {
    @InjectMocks
    private ItineraryService itineraryService;
    @Mock
    private ItineraryRepository itineraryRepository;
    @Mock
    private ItineraryMapper itineraryMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(itineraryMapper.toEntity(any(ItineraryDTO.class))).thenAnswer(invocation -> {
            ItineraryDTO dto = invocation.getArgument(0);
            Itinerary entity = new Itinerary();
            entity.setName(dto.getName());

            return entity;
        });

        when(itineraryMapper.toDomain(any(Itinerary.class))).thenAnswer(invocation -> {
            Itinerary entity = invocation.getArgument(0);
            ItineraryDTO dto = new ItineraryDTO();
            dto.setName(entity.getName());

            return dto;
        });
    }

    @Test
    void testGetAllItineraries() {
        List<Itinerary> itineraries = new ArrayList<>();
        Itinerary itinerary1 = new Itinerary();
        itinerary1.setName("Itinerary 1");
        Itinerary itinerary2 = new Itinerary();
        itinerary2.setName("Itinerary 2");
        itineraries.add(itinerary1);
        itineraries.add(itinerary2);

        when(itineraryRepository.findAll()).thenReturn(itineraries);

        List<ItineraryDTO> result = itineraryService.getAllItineraries();

        assertNotNull(result);
        assertEquals(itineraries.size(), result.size());
        assertEquals(itineraries.get(0).getName(), result.get(0).getName());
        assertEquals(itineraries.get(1).getName(), result.get(1).getName());
    }

    @Test
    void testGetItineraryById() {
        Long itineraryId = 1L;
        Itinerary itinerary = new Itinerary();
        itinerary.setId(itineraryId);
        itinerary.setName("Itinerary 1");
        when(itineraryRepository.findById(itineraryId)).thenReturn(Optional.of(itinerary));
        ItineraryDTO expectedDTO = new ItineraryDTO();
        expectedDTO.setName("Itinerary 1");
        when(itineraryMapper.toDomain(itinerary)).thenReturn(expectedDTO);

        ItineraryDTO result = itineraryService.getItineraryById(itineraryId);

        assertNotNull(result);
        assertEquals("Itinerary 1", result.getName());
    }
    
    @Test
    void testSearchItinerarySuccess() {
        String itineraryName = "%Adventure Itinerary%";
        List<Itinerary> foundItineraries = new ArrayList<>();
        Itinerary itinerary = new Itinerary();
        itinerary.setName("Adventure Itinerary");
        foundItineraries.add(itinerary);

        when(itineraryRepository.findAllIByNameIgnoreCaseLike(itineraryName)).thenReturn(Optional.of(foundItineraries));

        List<ItineraryDTO> expectedItineraryDTOs = foundItineraries.stream()
                .map(i -> {
                    ItineraryDTO dto = new ItineraryDTO();
                    dto.setName(i.getName());
                    return dto;
                })
                .collect(Collectors.toList());

        when(itineraryMapper.toDomain(any(Itinerary.class))).thenAnswer(invocation -> {
            Itinerary entity = invocation.getArgument(0);
            ItineraryDTO dto = new ItineraryDTO();
            dto.setName(entity.getName());
            return dto;
        });

        List<ItineraryDTO> result = itineraryService.searchByName(itineraryName);

        assertNotNull(result);
        assertEquals(expectedItineraryDTOs.size(), result.size());
        assertEquals(expectedItineraryDTOs.get(0).getName(), result.get(0).getName());
        verify(itineraryRepository, times(1)).findAllIByNameIgnoreCaseLike(itineraryName);
    }


}
