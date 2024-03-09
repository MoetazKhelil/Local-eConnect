package com.localeconnect.app.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadProfileRequest {

    @NotNull(message = "This is a required field!")
    private String profilePhoto;
}
