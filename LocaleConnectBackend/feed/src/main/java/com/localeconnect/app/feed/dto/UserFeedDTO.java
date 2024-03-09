package com.localeconnect.app.feed.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserFeedDTO {
    private Long id;
    @NotBlank(message = "This is a required field")
    private String firstName;
    @NotBlank(message = "This is a required field")
    private String lastName;
    @NotBlank(message = "This is a required field")
    private String userName;
    @NotBlank(message = "This is a required field")
    @Email(message = "Invalid email format")
    private String email;
    @NotNull(message = "This is a required field")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dateOfBirth;
    private String bio;
    private List<String> visitedCountries;
    @NotNull(message = "This is a required field")
    private boolean registeredAsLocalGuide;
    private List<String> languages;
    private List<UserFeedDTO> followers;
    private List<UserFeedDTO> followings;

}