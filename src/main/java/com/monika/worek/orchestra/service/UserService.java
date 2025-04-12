package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.auth.UserDTOMapper;
import com.monika.worek.orchestra.dto.UserDTO;
import com.monika.worek.orchestra.model.User;
import com.monika.worek.orchestra.model.UserRole;
import com.monika.worek.orchestra.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }
    public Optional<UserDTO> findUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTOMapper::map);
    }

    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserDTOMapper::map);
    }

    public boolean doesUserExist(String email) {
        return findUserByEmail(email).isPresent();
    }

    private boolean isCurrentUserAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    @Transactional
    public void updatePassword(String currentEmail, String newPassword) {
        User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

}
