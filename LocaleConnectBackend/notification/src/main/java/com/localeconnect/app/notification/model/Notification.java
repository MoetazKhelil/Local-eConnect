package com.localeconnect.app.notification.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id")
    private Long senderID;

    @Column(name = "receiver_id")
    private Long receiverID;

    @Column(name = "sent_at")
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime sentAt;

    private String title = "New Notification";

    private String message;

//    private String metadata;
    @Column(name = "is_read")
    private boolean isRead = false;
}
