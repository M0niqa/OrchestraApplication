package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.model.User;
import com.monika.worek.orchestra.model.UserRole;
import com.monika.worek.orchestra.repository.UserRepository;
import com.monika.worek.orchestra.repository.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRole userRole;

    @BeforeEach
    void setUp() {
        userRole = new UserRole(1L, "MUSICIAN");
        user = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Summer")
                .email("jane.summer@example.com")
                .password("encodedPassword")
                .roles(new HashSet<>())
                .build();
    }

    @Test
    void getAllBasicDTOUsers_whenUsersExist_thenShouldReturnSortedDtoList() {
        // given
        Set<UserRole> userRoles = new HashSet<>();
        User userB = User.builder().lastName("Summer").firstName("Jane").roles(userRoles).build();
        User userA = User.builder().lastName("Smith").firstName("John").roles(userRoles).build();
        when(userRepository.findAll()).thenReturn(List.of(userB, userA));

        // when
        List<UserBasicDTO> result = userService.getAllBasicDTOUsers();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getLastName()).isEqualTo("Smith");
        assertThat(result.get(1).getLastName()).isEqualTo("Summer");
    }

    @Test
    void findUserById_whenUserExists_thenShouldReturnUserBasicDto() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserBasicDTO result = userService.findUserById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Summer");
    }

    @Test
    void findUserById_whenUserDoesNotExist_thenShouldThrowException() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.findUserById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void findUserByEmail_whenUserExists_thenShouldReturnOptionalOfUserLoginDto() {
        // given
        when(userRepository.findByEmail("jane.summer@example.com")).thenReturn(Optional.of(user));

        // when
        Optional<UserLoginDTO> result = userService.findUserByEmail("jane.summer@example.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("jane.summer@example.com");
    }

    @Test
    void getUserEmailById_whenUserExists_thenShouldReturnEmailString() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        String email = userService.getUserEmailById(1L);

        // then
        assertThat(email).isEqualTo("jane.summer@example.com");
    }

    @Test
    void doesUserExist_whenUserExists_thenShouldReturnTrue() {
        // given
        when(userRepository.findByEmail("jane.summer@example.com")).thenReturn(Optional.of(user));

        // when
        boolean exists = userService.doesUserExist("jane.summer@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void updatePassword_whenUserExistsAndNewPasswordIsValid_thenShouldSaveUserWithEncodedPassword() {
        // given
        String email = "user@example.com";
        String newPassword = "newPassword123!";
        User existingUser = User.builder()
                .email(email)
                .password("oldEncodedPassword")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        userService.updatePassword(email, newPassword);

        // then
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isNotEqualTo("oldEncodedPassword");
        assertThat(savedUser.getPassword()).isNotEqualTo(newPassword);
        assertThat(savedUser.getPassword()).startsWith("{bcrypt}");
    }

    @Test
    void updatePassword_whenNewPasswordIsBlank_thenShouldSaveChangesWithoutUpdatingPassword() {
        // given
        String email = "user@example.com";
        String originalPassword = "password";
        User existingUser = User.builder()
                .email(email)
                .password(originalPassword)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        userService.updatePassword(email, "  ");

        // then
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getPassword()).isEqualTo(originalPassword);
    }

    @Test
    void updatePassword_whenUserDoesNotExist_thenShouldThrowEntityNotFoundException() {
        // given
        String nonExistentEmail = "ghost@example.com";
        String newPassword = "newPassword123!";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.updatePassword(nonExistentEmail, newPassword))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePassword_whenCalled_thenShouldSaveUserWithEncodedPassword() {
        // given
        when(userRepository.findByEmail("jane.summer@example.com")).thenReturn(Optional.of(user));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        userService.updatePassword("jane.summer@example.com", "newPassword123!");

        // then
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isNotEqualTo("newPassword123!");
        assertThat(savedUser.getPassword()).contains("{bcrypt}");
    }

    @Test
    void save_whenCalled_thenShouldInvokeRepositorySave() {
        // given
        User userToSave = new User();

        // when
        userService.save(userToSave);

        // then
        verify(userRepository, times(1)).save(userToSave);
    }

    @Test
    void deleteUserById_whenCalled_thenShouldInvokeRepositoryDelete() {
        // given
        Long userId = 1L;

        // when
        userService.deleteUserById(userId);

        // then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void addRoleToUser_whenUserDoesNotHaveRole_thenShouldAddRoleAndSave() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("MUSICIAN")).thenReturn(Optional.of(userRole));
        assertThat(user.getRoles()).isEmpty();

        // when
        userService.addRoleToUser(1L, "MUSICIAN");

        // then
        assertThat(user.getRoles()).contains(userRole);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addRoleToUser_whenUserAlreadyHasRole_thenShouldNotSave() {
        // given
        user.getRoles().add(userRole);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("MUSICIAN")).thenReturn(Optional.of(userRole));

        // when
        userService.addRoleToUser(1L, "MUSICIAN");

        // then
        verify(userRepository, never()).save(any());
    }

    @Test
    void removeRoleFromUser_whenUserHasRole_thenShouldRemoveRoleAndSave() {
        // given
        user.getRoles().add(userRole);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("MUSICIAN")).thenReturn(Optional.of(userRole));
        assertThat(user.getRoles()).contains(userRole);

        // when
        userService.removeRoleFromUser(1L, "MUSICIAN");

        // then
        assertThat(user.getRoles()).isEmpty();
        verify(userRepository, times(1)).save(user);
    }
}
