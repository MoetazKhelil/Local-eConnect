package com.localeconnect.app.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;


import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@Entity
@Table(name = "like_tab")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "liker_id")
    private Long likerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Post post;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Like )) return false;
        return id != null && id.equals(((Like) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

