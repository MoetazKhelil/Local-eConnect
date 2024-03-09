package com.localeconnect.app.feed.repository;

import com.localeconnect.app.feed.model.Post;
import com.localeconnect.app.feed.type.PostType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorID(Long authorId);

    List<Post> findByContentContainingIgnoreCase(String contentKeyword);

    List<Post> findByPostType(PostType postType);


}
