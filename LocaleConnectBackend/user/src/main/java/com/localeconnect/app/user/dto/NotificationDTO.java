package com.localeconnect.app.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationDTO implements Serializable {

    private Long id;

    @NotNull(message = "This is a required field")
    private Long senderID;

    @NotNull(message = "This is a required field")
    private Long receiverID;

    @NotNull(message = "This is a required field")
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime sentAt;

    @NotBlank(message = "This is a required field")
    private String message;

    private boolean isRead = false;
    private String title;
}

