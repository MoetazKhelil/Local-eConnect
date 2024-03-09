package com.localeconnect.app.feed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localeconnect.app.feed.dto.*;
import com.localeconnect.app.feed.service.FeedService;
import com.localeconnect.app.feed.type.PostType;
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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class FeedControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FeedService feedService;

    @InjectMocks
    private FeedController feedController;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(feedController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createRegularPost_ShouldReturnSuccess() throws Exception {
        RegularPostDTO regularPostDTO = new RegularPostDTO();
        regularPostDTO.setAuthorID(1L);
        regularPostDTO.setContent("Test content");
        regularPostDTO.setDate(LocalDateTime.now());
        regularPostDTO.setPostType(PostType.REGULAR);

        PostDTO newPostDTO = new PostDTO();
        newPostDTO.setId(1L);
        newPostDTO.setAuthorID(1L);
        newPostDTO.setContent("Test content");
        newPostDTO.setDate(LocalDateTime.now());
        newPostDTO.setPostType(PostType.REGULAR);

        given(feedService.createPost(any(RegularPostDTO.class))).willReturn(newPostDTO);

        mockMvc.perform(post("/api/feed/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regularPostDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Success!"));
    }
    @Test
    void deletePostById_ShouldReturnSuccess() throws Exception {
        PostDTO deletedPostDTO = new PostDTO();

        given(feedService.deletePost(anyLong())).willReturn(deletedPostDTO);

        mockMvc.perform(delete("/api/feed/delete/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"));
    }
    @Test
    void addComment_ShouldReturnSuccess() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText("This is a test comment");
        commentDTO.setAuthorID(1L);
        commentDTO.setDate(LocalDateTime.now());

        PostDTO updatedPostDTO = new PostDTO();
        given(feedService.addComment(anyLong(), any(CommentDTO.class))).willReturn(updatedPostDTO);

        mockMvc.perform(post("/api/feed/{postId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Success!"));
    }
    @Test
    void deleteComment_ShouldReturnSuccess() throws Exception {
        PostDTO updatedPostDTO = new PostDTO();

        given(feedService.deleteComment(anyLong(), anyLong())).willReturn(updatedPostDTO);

        mockMvc.perform(delete("/api/feed/{postId}/comment/{commentId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"));
    }
    @Test
    void shareTrip_ShouldReturnSuccess() throws Exception {
        TripDTO tripDTO = new TripDTO();
        tripDTO.setName("Test Trip");
        tripDTO.setDescription("Description of the trip");
        tripDTO.setDestination("Destination");
        tripDTO.setDurationInHours(10);
        tripDTO.setPlacesToVisit(List.of("Place 1", "Place 2"));
        tripDTO.setLanguages(List.of("English"));
        tripDTO.setTravelers(List.of(1L));
        tripDTO.setLocalguideId(1L);

        mockMvc.perform(post("/api/feed/share-trip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("authorId", "1")
                        .content(objectMapper.writeValueAsString(tripDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"));
    }
    @Test
    void shareItinerary_ShouldReturnSuccess() throws Exception {
        ItineraryDTO itineraryDTO = new ItineraryDTO();
        itineraryDTO.setName("Test Itinerary");
        itineraryDTO.setDescription("Itinerary description");
        itineraryDTO.setPlacesToVisit(List.of("Location 1", "Location 2"));

        mockMvc.perform(post("/api/feed/share-itinerary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("authorId", "1")
                        .content(objectMapper.writeValueAsString(itineraryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"));
    }

    @Test
    void shareMeetup_ShouldReturnSuccess() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        MeetupDTO meetupDTO = new MeetupDTO();
        meetupDTO.setName("Meetup of the month");
        meetupDTO.setDescription("Meetup Description");
        meetupDTO.setLocation("Location");
        meetupDTO.setStartTime(now.toString());
        meetupDTO.setEndTime(now.plusHours(2).toString());
        meetupDTO.setDate(new Date());
        meetupDTO.setCost(150.00);
        meetupDTO.setCreatorId(1L);

        mockMvc.perform(post("/api/feed/share-meetup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("authorId", "1")
                        .content(objectMapper.writeValueAsString(meetupDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"));
    }

    @Test
    void getPostById_ShouldReturnPost() throws Exception {
        PostDTO foundPostDTO = new PostDTO();
        given(feedService.getPostById(anyLong())).willReturn(foundPostDTO);

        mockMvc.perform(get("/api/feed/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"));
    }
    @Test
    void likePost_ShouldReturnUpdatedPost() throws Exception {
        LikeDTO likeDTO = new LikeDTO();
        likeDTO.setLikerId(1L);

        PostDTO postLiked = new PostDTO();

        given(feedService.likePost(anyLong(), any(LikeDTO.class))).willReturn(postLiked);

        mockMvc.perform(post("/api/feed/{postId}/like", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success!"));
    }

}
