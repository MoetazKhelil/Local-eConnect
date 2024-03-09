package com.localeconnect.app.user.service;

import com.localeconnect.app.user.dto.LocalguideDTO;
import com.localeconnect.app.user.dto.TravelerDTO;
import com.localeconnect.app.user.dto.UserDTO;
import com.localeconnect.app.user.exception.UserAlreadyExistsException;
import com.localeconnect.app.user.exception.UserDoesNotExistException;
import com.localeconnect.app.user.mapper.LocalguideMapper;
import com.localeconnect.app.user.mapper.TravelerMapper;
import com.localeconnect.app.user.mapper.UserMapper;
import com.localeconnect.app.user.model.Localguide;
import com.localeconnect.app.user.model.Traveler;
import com.localeconnect.app.user.model.User;
import com.localeconnect.app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserConfirmationEmail userConfirmationEmail;
    @Mock
    private TravelerMapper travelerMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private LocalguideMapper localguideMapper;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(bCryptPasswordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        when(travelerMapper.toEntity(any(TravelerDTO.class))).thenAnswer(invocation -> {
            TravelerDTO dto = invocation.getArgument(0);
            Traveler entity = new Traveler();
            entity.setUserName(dto.getUserName());
            entity.setEmail(dto.getEmail());
            entity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
            return entity;
        });

        when(travelerMapper.toDomain(any(Traveler.class))).thenAnswer(invocation -> {
            Traveler entity = invocation.getArgument(0);
            TravelerDTO dto = new TravelerDTO();
            dto.setUserName(entity.getUserName());
            dto.setEmail(entity.getEmail());
            return dto;
        });

        when(userMapper.toDomain(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUserName(user.getUserName());
            userDTO.setEmail(user.getEmail());
            return userDTO;
        });

        when(localguideMapper.toDomain(any(Localguide.class))).thenAnswer(invocation -> {
            Localguide entity = invocation.getArgument(0);
            LocalguideDTO dto = new LocalguideDTO();
            return dto;
        });
    }
    @Test
    void testRegisterTravelerSuccess() {
        // DTO
        TravelerDTO travelerDTO = new TravelerDTO();
        travelerDTO.setUserName("testUser");
        travelerDTO.setEmail("test@example.com");
        travelerDTO.setPassword("password");
        // Entity
        Traveler traveler = new Traveler();
        traveler.setUserName(travelerDTO.getUserName());
        traveler.setEmail(travelerDTO.getEmail());
        traveler.setPassword(bCryptPasswordEncoder.encode(travelerDTO.getPassword()));

        when(userRepository.existsByUserName(travelerDTO.getUserName())).thenReturn(false);
        when(userRepository.existsByEmail(travelerDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(Traveler.class))).thenReturn(traveler);

        TravelerDTO savedTraveler = userService.registerTraveler(travelerDTO);
        // Assertions
        assertNotNull(savedTraveler);
        assertEquals(travelerDTO.getUserName(), savedTraveler.getUserName());
        verify(userRepository, times(1)).save(any(Traveler.class));
    }
    @Test
    void testRegisterTravelerFailsWhenUserExists() {
        // DTO
        TravelerDTO travelerDTO = new TravelerDTO();
        travelerDTO.setUserName("testUser");
        travelerDTO.setEmail( "test@example.com");
        travelerDTO.setPassword("password");

        when(userRepository.existsByUserName(travelerDTO.getUserName())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerTraveler(travelerDTO);
        });
    }
    @Test
    void testGetUserByIdSuccess() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUserName("testUser");
        user.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testUser", result.getUserName());
    }
    @Test
    void testGetUserByIdFailsWhenNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () -> {
            userService.getUserById(userId);
        });
    }
    @Test
    void testDeleteUserSuccess() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(userId);
        verify(userRepository, times(1)).delete(user);
    }
    @Test
    void testDeleteUserFailsWhenNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () -> userService.deleteUser(userId));
    }
     @Test
    void testGetAllUsers() {
        List<User> users = new ArrayList<>();
        // User 1
        Long userId1 = 1L;
        User user1 = new User();
        user1.setId(userId1);
        user1.setUserName("testUser1");
        user1.setEmail("test1@example.com");
        user1.setPassword("password1");
        user1.setFirstName("Test1");
        user1.setLastName("User1");
        // User 2
        Long userId2 = 2L;
        User user2 = new User();
        user2.setId(userId2);
        user2.setUserName("testUser2");
        user2.setEmail("test2@example.com");
        user2.setPassword("password2");
        user2.setFirstName("Test2");
        user2.setLastName("User2");

        users.add(user1);
        users.add(user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testUser1", result.get(0).getUserName());
        assertEquals("test1@example.com", result.get(0).getEmail());
        assertEquals("testUser2", result.get(1).getUserName());
        assertEquals("test2@example.com", result.get(1).getEmail());

        verify(userRepository, times(1)).findAll();
    }
    @Test
    void testGetUserByEmail() {
        String email = "user@example.com";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUserName("userUser");
        user.setEmail(email);
        user.setPassword("pass");
        user.setFirstName("User");
        user.setLastName("User");
        user.setDateOfBirth(LocalDate.now());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }
//    @Test
    void testFollowUser() {

        Long followerId = 1L;
        User follower = new User();
        follower.setId(followerId);
        follower.setUserName("followerUser");
        follower.setEmail("follower@example.com");
        follower.setPassword("pass");
        follower.setFirstName("Follower");
        follower.setLastName("User");
        follower.setDateOfBirth(LocalDate.now());

        Long followeeId = 2L;
        User followee = new User();
        followee.setId(followeeId);
        followee.setUserName("followeeUser");
        followee.setEmail("followee@example.com");
        followee.setPassword("pass");
        followee.setFirstName("Followee");
        followee.setLastName("User");
        followee.setDateOfBirth(LocalDate.now());

        when(userRepository.findById(followerId)).thenReturn(Optional.of(follower));
        when(userRepository.findById(followeeId)).thenReturn(Optional.of(followee));

        userService.followUser(followerId, followeeId);

        assertTrue(followee.getFollowers().contains(follower));
        assertTrue(follower.getFollowing().contains(followee));
        verify(userRepository, times(1)).save(followee);
        verify(userRepository, times(1)).save(follower);
    }

    @Test
    void testRateLocalGuide() {
        Long guideId = 1L;
        Long travelerId = 2L;
        Double rating = 5.0;

        Localguide localguide = new Localguide();
        localguide.setId(guideId);
        localguide.setRatingsCount(0);
        localguide.setRatingsTotal(0.0);

        LocalguideDTO localguideDTO = new LocalguideDTO();
        localguideDTO.setRatingsCount(1);
        localguideDTO.setAverageRating(rating);

        when(userRepository.findById(guideId)).thenReturn(Optional.of(localguide));
        when(userRepository.findById(travelerId)).thenReturn(Optional.of(new User()));
        when(userRepository.save(any(Localguide.class))).then(invocation -> {
            Localguide savedGuide = invocation.getArgument(0);
            savedGuide.setRatingsCount(1);
            savedGuide.setRatingsTotal(rating);
            return savedGuide;
        });
        when(localguideMapper.toDomain(any(Localguide.class))).thenReturn(localguideDTO);

        LocalguideDTO ratedGuideDTO = userService.rateLocalGuide(guideId, travelerId, rating);

        assertNotNull(ratedGuideDTO);
        assertEquals(1, ratedGuideDTO.getRatingsCount());
        assertEquals(rating, ratedGuideDTO.getAverageRating());
        verify(userRepository).save(any(Localguide.class));
    }
    @Test
    void testGetAverageRatingOfLocalGuide() {
        Long guideId = 1L;
        Localguide localguide = new Localguide();
        localguide.setId(guideId);
        localguide.setRatingsCount(1);
        localguide.setRatingsTotal(5.0);

        when(userRepository.findById(guideId)).thenReturn(Optional.of(localguide));

        double averageRating = userService.getAverageRatingOfLocalGuide(guideId);
        assertEquals(5.0, averageRating, 0.01, "The average rating should be calculated correctly.");
        verify(userRepository).findById(guideId);
    }
    @Test
    void testGetRatingCountOfLocalGuide() {
        Long guideId = 1L;
        int expectedRatingsCount = 10;
        Localguide localguide = new Localguide();
        localguide.setId(guideId);
        localguide.setRatingsCount(expectedRatingsCount);

        when(userRepository.findById(guideId)).thenReturn(Optional.of(localguide));

        when(localguideMapper.toDomain(any(Localguide.class))).thenAnswer(invocation -> {
            Localguide entity = invocation.getArgument(0);
            LocalguideDTO dto = new LocalguideDTO();
            dto.setRatingsCount(entity.getRatingsCount());
            return dto;
        });

        int ratingCount = userService.getRatingCountOfLocalGuide(guideId);

        assertEquals(expectedRatingsCount, ratingCount, "The rating count should match the expected value.");
        verify(userRepository).findById(guideId);
    }
    @Test
    void testFilterLocalGuideByCity() {
        String city = "SampleCity";
        Localguide cityGuideOne = new Localguide();
        cityGuideOne.setId(1L);
        cityGuideOne.setUserName("CityGuideOne");
        cityGuideOne.setEmail("cityguideone@example.com");
        cityGuideOne.setPassword("password");
        cityGuideOne.setFirstName("City");
        cityGuideOne.setLastName("One");
        cityGuideOne.setDateOfBirth(LocalDate.of(1992, 3, 21));
        cityGuideOne.setLanguages(new ArrayList<>());
        cityGuideOne.setRatingsCount(0);
        cityGuideOne.setRatingsTotal(0.0);

        Localguide cityGuideTwo = new Localguide();
        cityGuideTwo.setId(2L);
        cityGuideTwo.setUserName("CityGuideTwo");
        cityGuideTwo.setEmail("cityguidetwo@example.com");
        cityGuideTwo.setPassword("password");
        cityGuideTwo.setFirstName("City");
        cityGuideTwo.setLastName("Two");
        cityGuideTwo.setDateOfBirth(LocalDate.of(1988, 7, 30));
        cityGuideTwo.setLanguages(new ArrayList<>());
        cityGuideTwo.setRatingsCount(0);
        cityGuideTwo.setRatingsTotal(0.0);

        List<Localguide> guides = List.of(cityGuideOne, cityGuideTwo);

        when(userRepository.findByCityContainingIgnoreCase(city)).thenReturn(guides);

        List<LocalguideDTO> filteredGuides = userService.filterLocalGuideByCity(city);

        assertNotNull(filteredGuides);
        assertEquals(guides.size(), filteredGuides.size());
        verify(userRepository).findByCityContainingIgnoreCase(city);
    }
    @Test
    void testSearchTravelersByFirstLastName() {
        String keyword = "User";
        User userOne = new User();
        userOne.setId(1L);
        userOne.setUserName("UserOne");
        userOne.setEmail("userone@example.com");
        userOne.setFirstName("User");
        userOne.setLastName("One");
        userOne.setDateOfBirth(LocalDate.of(1995, 10, 10));

        User userTwo = new User();
        userTwo.setId(2L);
        userTwo.setUserName("UserTwo");
        userTwo.setEmail("usertwo@example.com");
        userTwo.setFirstName("User");
        userTwo.setLastName("Two");
        userTwo.setDateOfBirth(LocalDate.of(1990, 2, 20));

        List<User> users = new ArrayList<>();
        users.add(userOne);
        users.add(userTwo);

        when(userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword)).thenReturn(users);

        List<UserDTO> foundUsers = userService.searchTravelersByFirstLastName(keyword);

        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.stream().anyMatch(u -> u.getEmail().equals("userone@example.com")));
        assertTrue(foundUsers.stream().anyMatch(u -> u.getEmail().equals("usertwo@example.com")));

        verify(userRepository).findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
    }
    @Test
    void testCheckUserId() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        boolean exists = userService.checkUserId(userId);

        assertTrue(exists);
        verify(userRepository, times(1)).findById(userId);
    }
}
