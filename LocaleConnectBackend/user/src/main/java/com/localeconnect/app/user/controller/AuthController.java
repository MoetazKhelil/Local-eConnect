package com.localeconnect.app.user.controller;

import com.localeconnect.app.user.auth.AuthenticationRequest;
import com.localeconnect.app.user.auth.AuthenticationResponse;
import com.localeconnect.app.user.dto.LocalguideDTO;
import com.localeconnect.app.user.dto.TravelerDTO;
import com.localeconnect.app.user.exception.UserDoesNotExistException;
import com.localeconnect.app.user.response_handler.ResponseHandler;
import com.localeconnect.app.user.service.AuthenticationService;
import com.localeconnect.app.user.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/user/auth")
public class AuthController {
    private final AuthenticationService authService;

    private final UserService userService;
    @PostMapping("/register-traveler")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> registerTraveler(@RequestBody @Valid TravelerDTO travelerDTO) {
        AuthenticationResponse response = authService.registerTraveler(travelerDTO);

        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, response, null);
    }

    @PostMapping("/register-localguide")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> registerLocalGuide(@RequestBody @Valid LocalguideDTO localguideDTO) {
        AuthenticationResponse response = authService.registerLocalguide(localguideDTO);

        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, response, null);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthenticationRequest request) {
        try {
            String token = authService.login(request);
            return ResponseHandler.generateResponse("Logged In!", HttpStatus.OK, token, null);
        } catch (UserDoesNotExistException e) {
            throw new UserDoesNotExistException("False credentials! Please try to login again.");
        }
    }

    @GetMapping("/exists/{userId}")
    public ResponseEntity<Object> checkUserExists(@PathVariable("userId") Long userId) {
        boolean exists = userService.checkUserId(userId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, exists, null);
    }

}
