package com.localeconnect.app.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localeconnect.app.user.auth.AuthenticationRequest;
import com.localeconnect.app.user.auth.AuthenticationResponse;
import com.localeconnect.app.user.dto.LocalguideDTO;
import com.localeconnect.app.user.dto.TravelerDTO;
import com.localeconnect.app.user.service.AuthenticationService;
import com.localeconnect.app.user.service.JwtUtil;
import com.localeconnect.app.user.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthenticationService authService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void registerTraveler_ShouldReturnSuccess() throws Exception {
        TravelerDTO travelerDTO = new TravelerDTO();
        travelerDTO.setFirstName("Test");
        travelerDTO.setLastName("22");
        travelerDTO.setUserName("testUser22");
        travelerDTO.setEmail("test22@test.com");
        travelerDTO.setPassword("password");
        travelerDTO.setDateOfBirth(LocalDate.of(1990, 1, 1)); // Example date

        AuthenticationResponse response = new AuthenticationResponse("dummyToken");
        given(authService.registerTraveler(any(TravelerDTO.class))).willReturn(response);

        mockMvc.perform(post("/api/user/auth/register-traveler")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(travelerDTO)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void registerLocalGuide_ShouldReturnSuccess() throws Exception {
        LocalguideDTO localguideDTO = new LocalguideDTO();
        localguideDTO.setFirstName("Test");
        localguideDTO.setLastName("22");
        localguideDTO.setUserName("testUser22");
        localguideDTO.setEmail("test22@test.com");
        localguideDTO.setPassword("password");
        localguideDTO.setDateOfBirth(LocalDate.of(1990, 1, 1)); // Example date

        AuthenticationResponse response = new AuthenticationResponse("dummyToken");
        given(authService.registerLocalguide(any(LocalguideDTO.class))).willReturn(response);

        mockMvc.perform(post("/api/user/auth/register-localguide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(localguideDTO)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void login_ShouldReturnToken() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("user@example.com", "password");
        String token = "authToken";

        given(authService.login(any(AuthenticationRequest.class))).willReturn(token);

        mockMvc.perform(post("/api/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(token));
    }

    @Test
    void checkUserExists_ShouldReturnTrue() throws Exception {
        given(userService.checkUserId(any(Long.class))).willReturn(true);

        mockMvc.perform(get("/api/user/auth/exists/{userId}", 1)) // Corrected to use get instead of post
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

}
