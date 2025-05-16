package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.auth.UserBasicDTOMapper;
import com.monika.worek.orchestra.auth.UserLoginDTOMapper;
import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.model.User;
import com.monika.worek.orchestra.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

    public List<UserBasicDTO> getAllBasicDTOUsers() {
        List<User> users= (List<User>) userRepository.findAll();
        return users.stream().sorted(Comparator.comparing(User::getLastName)
                .thenComparing(User::getFirstName)).map(UserBasicDTOMapper::mapToBasicDto).collect(Collectors.toList());
    }

    public UserBasicDTO findUserById(Long id) {
        return UserBasicDTOMapper.mapToBasicDto(userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found")));
    }

    public Optional<UserLoginDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email).map(UserLoginDTOMapper::mapToDto);
    }

    public UserBasicDTO getUserBasicDtoByEmail(String email) {
        return UserBasicDTOMapper.mapToBasicDto(userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found")));
    }

    public String getUserEmailById(Long id) {
        return userRepository.findById(id).map(User::getEmail).orElseThrow(() -> new EntityNotFoundException("User not found"));
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
        User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

}
