package com.localeconnect.app.feed.repository;

import com.localeconnect.app.feed.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
