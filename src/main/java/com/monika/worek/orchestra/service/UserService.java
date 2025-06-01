package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.dtoMappers.UserBasicDTOMapper;
import com.monika.worek.orchestra.dtoMappers.UserLoginDTOMapper;
import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.model.User;
import com.monika.worek.orchestra.model.UserRole;
import com.monika.worek.orchestra.repository.UserRepository;
import com.monika.worek.orchestra.repository.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private final UserRoleRepository roleRepository;

    public UserService(UserRepository userRepository, UserRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

    @Transactional
    public void updatePassword(String currentEmail, String newPassword) {
        User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (newPassword != null && !newPassword.isEmpty()) {
            PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
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

    @Transactional
    public void addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserRole role = roleRepository.findByName(roleName.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserRole role = roleRepository.findByName(roleName.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        user.getRoles().remove(role);
        userRepository.save(user);
    }

}
