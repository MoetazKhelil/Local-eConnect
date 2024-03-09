package com.localeconnect.app.feed.service;

import com.localeconnect.app.feed.dto.*;
import com.localeconnect.app.feed.dto.UserFeedDTO;
import com.localeconnect.app.feed.exceptions.LogicException;
import com.localeconnect.app.feed.exceptions.ResourceNotFoundException;
import com.localeconnect.app.feed.exceptions.ValidationException;
import com.localeconnect.app.feed.mapper.CommentMapper;
import com.localeconnect.app.feed.mapper.LikeMapper;
import com.localeconnect.app.feed.mapper.PostMapper;
import com.localeconnect.app.feed.model.Comment;
import com.localeconnect.app.feed.model.Like;
import com.localeconnect.app.feed.model.Post;
import com.localeconnect.app.feed.repository.CommentRepository;
import com.localeconnect.app.feed.repository.PostRepository;
import com.localeconnect.app.feed.type.PostType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class FeedService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;
    private final WebClient webClient;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public PostDTO createPost(RegularPostDTO regularPost) {
        Post post = postMapper.toEntity(regularPost);
        long authorId = post.getAuthorID();
        if (!checkUserId(authorId))
            throw new ResourceNotFoundException("No User Found with id: " + authorId + "!");

        List<String> images = post.getImages();

        if (!images.isEmpty()) {
            List<String> imagesUrls = new ArrayList<>();

            for (String image : images) {
                GCPResponseDTO gcpResponse = saveImageToGCP(image);
                String imageUrl = gcpResponse.getData();
                imagesUrls.add(imageUrl);
            }
            post.setImages(imagesUrls);
        }

        Post createdPost = postRepository.save(post);

        return postMapper.toDomain(createdPost);
    }


    public PostDTO deletePost(Long postId) {
        Optional<Post> optional = postRepository.findById(postId);
        if (optional.isEmpty())
            throw new ResourceNotFoundException("No Post Found with id: " + postId + "!");

        Post actualPost = optional.get();
        postRepository.delete(actualPost);

        return postMapper.toDomain(actualPost);
    }

    public PostDTO addComment(Long postId, CommentDTO commentDTO) {
        Optional<Post> optional = postRepository.findById(postId);
        if (optional.isEmpty())
            throw new ResourceNotFoundException("No Post Found with id: " + postId + "!");

        Post actualPost = optional.get();
        Comment comment = commentMapper.toEntity(commentDTO);

        long authorId = comment.getAuthorID();
        if (!checkUserId(authorId))
            throw new ResourceNotFoundException("User with id " + authorId + " does not exist!");

        actualPost.addComment(comment);
        postRepository.save(actualPost);

        return postMapper.toDomain(actualPost);
    }

    public PostDTO deleteComment(Long postId, Long commentId) {
        Optional<Post> optional = postRepository.findById(postId);
        if (optional.isEmpty())
            throw new ResourceNotFoundException("No Post Found with id: " + postId + "!");

        Post actualPost = optional.get();

        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty())
            throw new ResourceNotFoundException("No Comment Found with id: " + commentId + "!");

        Comment actualComment = comment.get();
        if (!Objects.equals(actualComment.getPost().getId(), postId))
            throw new LogicException("Comment with id " + commentId + " Does Not Belong To Post with Id: " + postId);

        actualPost.removeComment(actualComment);

        return postMapper.toDomain(actualPost);
    }

    public PostDTO shareTrip(TripDTO trip, Long authorId) {
        if (!checkUserId(authorId))
            throw new ResourceNotFoundException("User with id " + authorId + " does not exist!");
        Post post = new Post();
        post.setAuthorID(authorId);
        post.setPostType(PostType.TRIP);
        post.setContent(createContentFromTrip(trip));
        post.setDate(LocalDateTime.now());
        post.setImages(trip.getImageUrls());

        Post createdPost = postRepository.save(post);
        return postMapper.toDomain(createdPost);
    }

    public PostDTO shareItinerary(ItineraryDTO itinerary, Long authorId) {
        if (!checkUserId(authorId))
            throw new ResourceNotFoundException("User with id " + authorId + " does not exist!");
        Post post = new Post();
        post.setAuthorID(authorId);
        post.setPostType(PostType.ITINERARY);
        post.setContent(createContentFromItinerary(itinerary));
        post.setDate(LocalDateTime.now());
        post.setImages(itinerary.getImageUrls());
        Post createdPost = postRepository.save(post);
        return postMapper.toDomain(createdPost);
    }

    public PostDTO shareMeetup(MeetupDTO meetup, Long authorId) {
        if (!checkUserId(authorId))
            throw new ResourceNotFoundException("User with id " + authorId + " does not exist!");
        Post post = new Post();
        post.setAuthorID(authorId);
        post.setPostType(PostType.MEETUP);
        post.setContent(createContentFromMeetup(meetup));
        post.setDate(LocalDateTime.now());

        Post createdPost = postRepository.save(post);
        return postMapper.toDomain(createdPost);
    }

    public PostDTO getPostById(Long postId) {
        Post postToFind = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("No Post Found with id: " + postId + "!"));
        return postMapper.toDomain(postToFind);
    }

    public List<String> getPostLikes(Long postId) {
        PostDTO post = getPostById(postId);
        List<String> usersLikedThePost = new ArrayList<>();

        for (Like like : post.getLikes()) {

            usersLikedThePost.add(getUserNameById(like.getLikerId()));
        }
        return usersLikedThePost;
    }

    public PostDTO likePost(Long postId, LikeDTO likeDTO) {
        Post postToFind = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("No Post Found with id: " + postId + "!"));
        Long likerId = likeDTO.getLikerId();

        if (!checkUserId(likerId))
            throw new ResourceNotFoundException("User with id " + likerId + " does not exist!");

        boolean alreadyLiked = postToFind.getLikes().stream()
                .anyMatch(like -> like.getLikerId().equals(likerId));
        if (alreadyLiked) {
            throw new ValidationException("User with id " + likerId + " already likes this post.");
        }

        Like like = likeMapper.toEntity(likeDTO);
        like.setPost(postToFind); // Ensure the like is associated with the post
        postToFind.addLike(like);
        postRepository.save(postToFind);
        return postMapper.toDomain(postToFind);
    }


    public PostDTO unlikePost(Long postId, LikeDTO likeDTO) {
        Post postToFind = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("No Post Found with id: " + postId + "!"));
        Long likerId = likeDTO.getLikerId();

        if (!checkUserId(likerId))
            throw new ResourceNotFoundException("User with id " + likerId + " does not exist!");

        Like likeToRemove = postToFind.getLikes().stream()
                .filter(like -> like.getLikerId().equals(likerId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("User with id " + likerId + " does not like this post."));

        postToFind.removeLike(likeToRemove);
        postRepository.save(postToFind);
        return postMapper.toDomain(postToFind);
    }

    public List<PostDTO> getPostsByAuthorId(Long authorId) {
        if (!checkUserId(authorId))
            throw new ResourceNotFoundException("User with id " + authorId + " does not exist!");

        return postRepository.findByAuthorID(authorId).stream().map(postMapper::toDomain).toList();
    }

    public List<PostDTO> searchPosts(String keyword) {
        return postRepository.findByContentContainingIgnoreCase(keyword)
                .stream().map(postMapper::toDomain).collect(Collectors.toList());
    }

    public List<PostDTO> filterPosts(PostType postType) {
        List<Post> posts = postRepository.findByPostType(postType);
        return posts.stream()
                .map(postMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<PostDTO> generateUserFeed(Long userId) {
        List<UserFeedDTO> following = getFollowing(userId);

        List<PostDTO> postList = following.stream()
                .map(UserFeedDTO::getId)
                .flatMap(authorId -> getPostsByAuthorId(authorId).stream())
                .collect(Collectors.toList());

        for (PostDTO p : getPostsByAuthorId(userId)) {
            postList.add(p);
        }

        return postList;
    }

    private List<UserFeedDTO> getFollowing(Long userId) {
        if (!checkUserId(userId))
            throw new ResourceNotFoundException("User with id " + userId + " does not exist!");

        GetFollowingResponseDTO res = webClient.get()
                .uri("http://user-service:8084/api/user/secured/{userId}/following", userId)
                .retrieve()
                .bodyToMono(GetFollowingResponseDTO.class).block();

        return res.getData();
    }

    /* public Mono<List<PostDTO>> generateUserFeed(Long userId) {
         return getFollowing(userId)
                 .flatMapMany(Flux::fromIterable)
                 .flatMap(this::getPostsByAuthorId)
                 .collectList()
                 .flatMap(feed -> updateFeedCache(userId, feed).thenReturn(feed));
     }

     private Mono<List<Long>> getFollowing(Long userId) {
         return webClient.get()
                 .uri("http://user-service:8084/api/user/secured/{userId}/following", userId)
                 .retrieve()
                 .bodyToMono(new ParameterizedTypeReference<List<Long>>() {});
     }

     private List<PostDTO> getPostsByAuthorId(Long authorId) {
         List<PostDTO> result = new ArrayList<>();
         List<Post> posts = postRepository.findByAuthorID(authorId);
         for(Post p : posts) {
             result.add(postMapper.toDomain(p));
         }
         return Flux.fromIterable(result);
     }

     private Mono<Boolean> updateFeedCache(Long userId, List<PostDTO> feed) {
         ObjectMapper objectMapper = new ObjectMapper();
         return Mono.fromCallable(() -> objectMapper.writeValueAsString(feed))
                 .flatMap(serializedFeed -> reactiveRedisTemplate.opsForValue().set("feed:" + userId, serializedFeed));
     }
 */
    private String createContentFromItinerary(ItineraryDTO dto) {
        return "Itinerary: " + dto.getName() +
                ", Days: " + dto.getNumberOfDays() +
                ", Places: " + dto.getPlacesToVisit() +
                ", Description: " + dto.getDescription();
    }

    private String createContentFromTrip(TripDTO dto) {
        return "Trip: " + dto.getName() +
                ", Guide: " + dto.getLocalguideId() +
                ", Departure: " + dto.getDepartureTime() +
                ", Destination: " + dto.getDestination() +
                ", Duration: " + dto.getDurationInHours() + " hours";
    }

    private String createContentFromMeetup(MeetupDTO dto) {
        return "Meetup: " + dto.getName() +
                ", Location: " + dto.getLocation() +
                ", Date: " + dto.getDate().toString() +
                ", Cost: " + String.format("%.2f", dto.getCost());
    }

    private boolean checkUserId(Long userId) {
        System.out.println(userId);
        CheckUserExistsResponseDTO res = this.webClient.get()
                .uri("http://user-service:8084/api/user/auth/exists/{userId}", userId)
                .retrieve().bodyToMono(CheckUserExistsResponseDTO.class).block();

        Boolean check = res.getData();
        return check != null && check;
    }

    private String getUserNameById(Long id) {
        if (!checkUserId(id))
            throw new ResourceNotFoundException("User with id " + id + " does not exist!");

        GetUserByIdResponseDTO res = this.webClient.get()
                .uri("http://user-service:8084/api/user/secured/{userId}", id)
                .retrieve().bodyToMono(GetUserByIdResponseDTO.class).block();

        UserFeedDTO user = res.getData();
        return user.getUserName();
    }

    private GCPResponseDTO saveImageToGCP(String image) {
        ResponseEntity<GCPResponseDTO> responseEntity = webClient.post()
                .uri("http://gcp-service:5005/api/gcp/?filename=itinerary")
                .bodyValue(image)
                .retrieve()
                .toEntity(GCPResponseDTO.class)
                .block();
        return responseEntity.getBody();
    }

}

