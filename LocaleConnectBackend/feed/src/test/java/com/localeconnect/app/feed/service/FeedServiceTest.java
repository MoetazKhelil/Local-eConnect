package com.localeconnect.app.feed.service;

import com.localeconnect.app.feed.dto.PostDTO;
import com.localeconnect.app.feed.mapper.CommentMapper;
import com.localeconnect.app.feed.mapper.PostMapper;
import com.localeconnect.app.feed.model.Post;
import com.localeconnect.app.feed.repository.CommentRepository;
import com.localeconnect.app.feed.repository.PostRepository;
import com.localeconnect.app.feed.type.PostType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class FeedServiceTest {

    @InjectMocks
    private FeedService feedService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private CommentMapper commentMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeletePost() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        PostDTO postDTO = new PostDTO();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDomain(any(Post.class))).thenReturn(postDTO);

        PostDTO result = feedService.deletePost(postId);

        assertNotNull(result);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void testGetPostById() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        PostDTO postDTO = new PostDTO();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toDomain(any(Post.class))).thenReturn(postDTO);

        PostDTO result = feedService.getPostById(postId);

        assertNotNull(result);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testSearchPosts() {
        String keyword = "test";
        List<Post> posts = List.of(new Post());
        when(postRepository.findByContentContainingIgnoreCase(keyword)).thenReturn(posts);
        when(postMapper.toDomain(any(Post.class))).thenReturn(new PostDTO());

        List<PostDTO> result = feedService.searchPosts(keyword);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(postRepository, times(1)).findByContentContainingIgnoreCase(keyword);
    }

    @Test
    void testFilterPosts() {
        PostType postType = PostType.TRIP;
        List<Post> posts = List.of(new Post());
        when(postRepository.findByPostType(postType)).thenReturn(posts);
        when(postMapper.toDomain(any(Post.class))).thenReturn(new PostDTO());

        List<PostDTO> result = feedService.filterPosts(postType);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(postRepository, times(1)).findByPostType(postType);
    }
}
