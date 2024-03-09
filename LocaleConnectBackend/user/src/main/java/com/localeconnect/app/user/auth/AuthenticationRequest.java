package com.localeconnect.app.user.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationRequest {
    @NotBlank
    private String email;
    @NotBlank
    String password;
}
