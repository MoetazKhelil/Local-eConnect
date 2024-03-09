package com.localeconnect.app.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localeconnect.app.user.dto.UserDTO;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnUsers() throws Exception {
        List<UserDTO> users = List.of(
                UserDTO.builder().id(1L).firstName("Alice").lastName("Smith").userName("aliceSmith").email("alice@example.com").build(),
                UserDTO.builder().id(2L).firstName("Bob").lastName("Jones").userName("bobbyJ").email("bob@example.com").build()
        );
        given(userService.getAllUsers()).willReturn(users);

        mockMvc.perform(get("/api/user/secured/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].id").value(users.get(0).getId()))
                .andExpect(jsonPath("$.data[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.data[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.data[0].userName").value("aliceSmith"))
                .andExpect(jsonPath("$.data[0].email").value("alice@example.com"))
                .andExpect(jsonPath("$.data[1].id").value(users.get(1).getId()))
                .andExpect(jsonPath("$.data[1].firstName").value("Bob"))
                .andExpect(jsonPath("$.data[1].lastName").value("Jones"))
                .andExpect(jsonPath("$.data[1].userName").value("bobbyJ"))
                .andExpect(jsonPath("$.data[1].email").value("bob@example.com"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        UserDTO userDTO = UserDTO.builder().id(1L).firstName("Charlie").lastName("Brown").userName("charlieB").email("charlie@example.com").build();
        given(userService.getUserById(1L)).willReturn(userDTO);

        mockMvc.perform(get("/api/user/secured/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.firstName").value("Charlie"))
                .andExpect(jsonPath("$.data.lastName").value("Brown"))
                .andExpect(jsonPath("$.data.userName").value("charlieB"))
                .andExpect(jsonPath("$.data.email").value("charlie@example.com"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .firstName("TestFirstName")
                .lastName("TestLastName")
                .userName("TestUserName")
                .email("test@example.com")
                .password("TestPassword")
                .dateOfBirth(LocalDate.now().minusYears(20))
                .build();
        given(userService.updateUser(any(UserDTO.class))).willReturn(userDTO);

        mockMvc.perform(put("/api/user/secured/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("TestFirstName"))
                .andExpect(jsonPath("$.data.lastName").value("TestLastName"))
                .andExpect(jsonPath("$.data.userName").value("TestUserName"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.dateOfBirth").value(userDTO.getDateOfBirth().toString()));
    }

    @Test
    void deleteUser_ShouldReturnSuccess() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/user/secured/delete/{userId}", 1))
                .andExpect(status().isOk());
    }
}
