package com.localeconnect.app.user.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDTO {
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
    //@JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateOfBirth;
    private String bio;
    @NotBlank(message = "This is a required field")
    private String password;
    private List<String> visitedCountries;
    @NotNull(message = "This is a required field")
    private boolean registeredAsLocalGuide;
    private List<String> languages;
    private List<UserDTO> followers;
    private List<UserDTO> followings;
    private Boolean isEnabled;
    private String profilePicture;
}
