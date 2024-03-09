package com.localeconnect.app.user.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
public class AuthenticationResponse {
    @JsonProperty("access_token")
    private String accessToken;

    public AuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
