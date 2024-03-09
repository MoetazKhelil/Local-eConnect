package com.localeconnect.app.user.service;

import com.localeconnect.app.user.auth.AuthenticationRequest;
import com.localeconnect.app.user.auth.AuthenticationResponse;
import com.localeconnect.app.user.dto.LocalguideDTO;
import com.localeconnect.app.user.dto.TravelerDTO;
import com.localeconnect.app.user.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterTraveler() {

        TravelerDTO travelerDTO = new TravelerDTO();
        travelerDTO.setUserName("testUser");
        travelerDTO.setEmail("test@example.com");
        travelerDTO.setPassword("password");

        when(jwtUtil.generateToken(anyString())).thenReturn("dummy-token");
        when(userService.registerTraveler(any(TravelerDTO.class))).thenReturn(travelerDTO);

        AuthenticationResponse response = authenticationService.registerTraveler(travelerDTO);

        assertNotNull(response);
        assertEquals("dummy-token", response.getAccessToken());
        verify(userService).registerTraveler(travelerDTO);
        verify(jwtUtil).generateToken(travelerDTO.getUserName());
    }

    @Test
    void testRegisterLocalGuide() {
        LocalguideDTO localguideDTO = new LocalguideDTO();
        localguideDTO.setUserName("localGuideUser");
        localguideDTO.setEmail("localguide@example.com");
        localguideDTO.setPassword("securePassword");
        localguideDTO.setLanguages(List.of("English", "Spanish"));
        localguideDTO.setDateOfBirth(LocalDate.now().minusYears(20));

        when(jwtUtil.generateToken(anyString())).thenReturn("dummy-token-for-localguide");
        when(userService.registerLocalguide(any(LocalguideDTO.class))).thenReturn(localguideDTO);

        AuthenticationResponse response = authenticationService.registerLocalguide(localguideDTO);

        assertNotNull(response);
        assertEquals("dummy-token-for-localguide", response.getAccessToken());
        verify(userService).registerLocalguide(localguideDTO);
        verify(jwtUtil).generateToken(localguideDTO.getUserName());
    }
    @Test
    void testLoginSuccess() {
        AuthenticationRequest request = new AuthenticationRequest("user@example.com", "password");
        String expectedToken = "auth-token";

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        when(jwtUtil.generateToken(request.getEmail())).thenReturn(expectedToken);

        String actualToken = authenticationService.login(request);

        assertNotNull(actualToken, "The returned token should not be null");
        assertEquals(expectedToken, actualToken, "The expected token does not match the actual token");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(request.getEmail());
    }

    @Test
    void testLoginFailure() {
        AuthenticationRequest request = new AuthenticationRequest("user@example.com", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new ValidationException("Bad credentials"));

        assertThrows(ValidationException.class, () -> authenticationService.login(request),
                "Expected login to throw ValidationException due to bad credentials");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(jwtUtil, times(0)).generateToken(request.getEmail());
    }

}
