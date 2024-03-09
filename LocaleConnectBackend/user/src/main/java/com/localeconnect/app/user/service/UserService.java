package com.localeconnect.app.user.service;

import com.localeconnect.app.user.config.RabbitConfig;
import com.localeconnect.app.user.dto.*;
import com.localeconnect.app.user.exception.LogicException;
import com.localeconnect.app.user.exception.UserAlreadyExistsException;
import com.localeconnect.app.user.exception.UserDoesNotExistException;
import com.localeconnect.app.user.exception.ValidationException;
import com.localeconnect.app.user.mapper.LocalguideMapper;
import com.localeconnect.app.user.mapper.TravelerMapper;
import com.localeconnect.app.user.mapper.UserMapper;
import com.localeconnect.app.user.model.Localguide;
import com.localeconnect.app.user.model.Traveler;
import com.localeconnect.app.user.model.User;
import com.localeconnect.app.user.rabbit.RabbitMQMessageProducer;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import com.localeconnect.app.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserConfirmationEmail userConfirmationEmail;
    private final UserMapper userMapper;
    private final TravelerMapper travelerMapper;
    private final LocalguideMapper localguideMapper;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    private final WebClient webClient;

    public TravelerDTO registerTraveler(TravelerDTO travelerDTO) {
        if (userRepository.existsByUserName(travelerDTO.getUserName())) {
            throw new UserAlreadyExistsException("A user with the given username already exists.");
        }

        if (userRepository.existsByEmail(travelerDTO.getEmail())) {
            throw new UserAlreadyExistsException("A user with the given email already exists");
        }

        userConfirmationEmail.sendConfirmationEmail(travelerDTO);
        Traveler traveler = travelerMapper.toEntity(travelerDTO);
        traveler.setPassword(BCrypt.hashpw(traveler.getPassword(), BCrypt.gensalt()));
        userRepository.save(traveler);

        return travelerMapper.toDomain(traveler);
    }

    public LocalguideDTO registerLocalguide(LocalguideDTO localguideDTO) {
        if (userRepository.existsByUserName(localguideDTO.getUserName())) {
            throw new UserAlreadyExistsException("A user with the given username already exists.");
        }

        if (userRepository.existsByEmail(localguideDTO.getEmail())) {
            throw new UserAlreadyExistsException("A user with the given email already exists");
        }

        if (localguideDTO.getLanguages().size() < 2 || localguideDTO.getDateOfBirth().plusYears(18).isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot be a local guide: must speak at least 2 languages and be at least 18 years old.");
        }

        userConfirmationEmail.sendConfirmationEmail(localguideDTO);
        localguideDTO.calcAverageRating();
        Localguide localguide = localguideMapper.toEntity(localguideDTO);
        localguide.setPassword(BCrypt.hashpw(localguide.getPassword(), BCrypt.gensalt()));
        userRepository.save(localguide);

        return localguideMapper.toDomain(localguide);
    }

    public List<UserDTO> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserDoesNotExistException("User with the given id does not exist"));
        return userMapper.toDomain(user);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserDoesNotExistException("User with the given email does not exist"));
        return userMapper.toDomain(user);
    }


    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));
        userRepository.delete(user);
    }

    public void followUser(Long followerId, Long userId) {
        User userToFollow = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserDoesNotExistException("Follower not found"));
        if (userId.equals(followerId)) {
            throw new LogicException("User cannot follow themselves");
        }
        if (!userToFollow.getFollowers().contains(follower)) {
            userToFollow.getFollowers().add(follower);
            follower.getFollowing().add(userToFollow);
            userRepository.save(userToFollow);
            userRepository.save(follower);

            NotificationDTO newNotification = new NotificationDTO();
            newNotification.setTitle("New Follow Notification");
            newNotification.setMessage("Traveller " + followerId + " Followed You!");
            newNotification.setSentAt(LocalDateTime.now());
            newNotification.setReceiverID(userId);
            newNotification.setSenderID(followerId);
            rabbitMQMessageProducer.publish(newNotification, RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY);
        } else {
            throw new ValidationException("Already following this user");
        }
    }

    public void unfollowUser(Long followerId, Long userId) {
        if (userId.equals(followerId)) {
            throw new IllegalArgumentException("User cannot unfollow themselves");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserDoesNotExistException("Follower not found"));

        if (user.getFollowers().contains(follower)) {
            user.getFollowers().remove(follower);
            follower.getFollowing().remove(user);

            userRepository.save(user);
            userRepository.save(follower);
        } else {
            throw new ValidationException("User is not following the specified followee");
        }
    }

    public UserDTO updateUser(UserDTO userDTO) {
        User existingUser = userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new UserDoesNotExistException("User with email " + userDTO.getEmail() + " does not exist"));
        if (existingUser.isRegisteredAsLocalGuide()) {
            ((LocalguideDTO) userDTO).calcAverageRating();
        }
        userMapper.updateUserFromDto(userDTO, existingUser);
        User savedUser = userRepository.save(existingUser);
        return userMapper.toDomain(savedUser);
    }

    public LocalguideDTO rateLocalGuide(Long guideId, Long travelerId, Double rating) {
        Localguide localguide = userRepository.findById(guideId)
                .filter(user -> user instanceof Localguide)
                .map(user -> (Localguide) user)
                .orElseThrow(() -> new UserDoesNotExistException("LocalGuide with id " + guideId + " does not exist"));

        if (!checkUserId(travelerId))
            throw new UserDoesNotExistException("Traveler with id " + travelerId + " does not exist");

        localguide.addRating(rating);
        localguide.calcAverageRating();

        userRepository.save(localguide);
        return localguideMapper.toDomain(localguide);
    }

    public List<LocalguideDTO> getAllGuides() {
        List<Localguide> guides = userRepository.findByRegisteredAsLocalGuide(true).stream()
                .filter(user -> user instanceof Localguide)
                .map(user -> (Localguide) user)
                .toList();
        return guides.stream()
                .map(localguideMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<LocalguideDTO> filterLocalGuideByCity(String keyword) {
        List<Localguide> guides = userRepository.findByCityContainingIgnoreCase(keyword).stream()
                .filter(Localguide.class::isInstance)
                .map(Localguide.class::cast)
                .toList();
        return guides.stream()
                .map(localguideMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<UserDTO> searchTravelersByFirstLastName(String keyword) {
        List<User> travelers = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword).stream()
                .filter(user -> !user.isRegisteredAsLocalGuide())
                .toList();
        return travelers.stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User with the id " + userId + "does not Exist!"));
        return user.getFollowers().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getFollowing(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User with the id " + userId + "does not Exist!"));
        return user.getFollowing().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    public UserDTO getProfileDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("User with the given id does not exist"));

        if (user instanceof Localguide) {
            LocalguideDTO localguideDTO = LocalguideDTO.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .userName(user.getUserName())
                    .bio(user.getBio())
                    .ratingsCount(((Localguide) user).getRatingsCount())
                    .ratingsTotal(((Localguide) user).getRatingsTotal())
                    .registeredAsLocalGuide(true)
                    .languages(user.getLanguages())
                    .followers(user.getFollowers().stream().map(userMapper::toDomain).toList())
                    .followings(user.getFollowing().stream().map(userMapper::toDomain).toList())
                    .profilePicture(user.getProfilePicture())
                    .build();
            localguideDTO.calcAverageRating();

            return localguideDTO;
        }

        TravelerDTO travelerDTO = TravelerDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .bio(user.getBio())
                .registeredAsLocalGuide(false)
                .languages(user.getLanguages())
                .followers(user.getFollowers().stream().map(userMapper::toDomain).toList())
                .followings(user.getFollowing().stream().map(userMapper::toDomain).toList())
                .profilePicture(user.getProfilePicture())
                .build();

        return travelerDTO;
    }

    public double getAverageRatingOfLocalGuide(Long guideId) {
        Localguide localguide = userRepository.findById(guideId)
                .filter(user -> user instanceof Localguide)
                .map(user -> (Localguide) user)
                .orElseThrow(() -> new UserDoesNotExistException("LocalGuide with id " + guideId + " does not exist"));

        if (localguide.getRatingsCount() == 0) {
            return 0; // NO division by zero
        }
        return localguide.getRatingsTotal() / localguide.getRatingsCount();
    }

    public int getRatingCountOfLocalGuide(Long guideId) {
        Localguide localguide = userRepository.findById(guideId)
                .filter(user -> user instanceof Localguide)
                .map(user -> (Localguide) user)
                .orElseThrow(() -> new UserDoesNotExistException("LocalGuide with id " + guideId + " does not exist"));
        return localguide.getRatingsCount();
    }


    public boolean checkUserId(Long userId) {
        return userRepository.findById(userId).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("user not found with email :" + email);
        } else {
            return new UserPrincipalDTO(user.get());
        }
    }

    public UserDTO uploadProfilePicture(Long travelerId, UploadProfileRequest profile) {

        if (!checkUserId(travelerId))
            throw new UserDoesNotExistException("Traveler with id " + travelerId + " does not exist");

        Optional<User> user = userRepository.findById(travelerId);

        if (user.isEmpty())
            throw new UserDoesNotExistException("Traveler with id " + travelerId + " does not exist");

        User actualUser = user.get();

        String image = profile.getProfilePhoto();
        GCPResponseDTO imaegUrl = saveImageToGCP(image);
        actualUser.setProfilePicture(imaegUrl.getData());
        userRepository.save(actualUser);
        return userMapper.toDomain(actualUser);
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