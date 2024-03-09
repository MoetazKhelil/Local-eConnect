package com.localeconnect.app.feed.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.localeconnect.app.feed.model.Comment;
import com.localeconnect.app.feed.model.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LikeDTO {

    private Long id;

    @NotNull(message = "This is a required field")
    private Long likerId;

    @JsonIgnoreProperties("likes")
    private Post post;

}



