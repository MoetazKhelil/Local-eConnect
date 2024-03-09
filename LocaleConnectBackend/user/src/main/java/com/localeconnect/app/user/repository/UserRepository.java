package com.localeconnect.app.user.repository;

import com.localeconnect.app.user.model.Localguide;
import com.localeconnect.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByRegisteredAsLocalGuide(boolean isLocalGuide);
    List<Localguide> findByCityContainingIgnoreCase(String city);
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}
