package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.auth.UserBasicDTOMapper;
import com.monika.worek.orchestra.auth.UserDTOMapper;
import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.dto.UserDTO;
import com.monika.worek.orchestra.model.User;
import com.monika.worek.orchestra.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserBasicDTO> getAllUsers() {
        List<User> users= (List<User>) userRepository.findAll();
        return users.stream().map(UserBasicDTOMapper::mapToBasicDto).collect(Collectors.toList());

    }

    public UserBasicDTO findUserById(Long id) {
        return UserBasicDTOMapper.mapToBasicDto(userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found")));
    }

    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email).map(UserDTOMapper::mapToDto);

    }

    public UserBasicDTO getUserBasicDtoByEmail(String email) {
        return UserBasicDTOMapper.mapToBasicDto(findUserByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found")));
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
