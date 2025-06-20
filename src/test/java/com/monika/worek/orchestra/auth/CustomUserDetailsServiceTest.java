package com.monika.worek.orchestra.auth;

import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.model.UserRole;
import com.monika.worek.orchestra.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_whenUserFound_thenShouldReturnUserDetails() {
        // given
        String email = "test@example.com";
        String password = "encodedPassword";

        Set<UserRole> roles = Set.of(UserRole.builder().name("ADMIN").build(),
                UserRole.builder().name("MUSICIAN").build());

        UserLoginDTO mockUser = new UserLoginDTO(email, password, roles);

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(mockUser));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MUSICIAN")));
        verify(userService, times(1)).findUserByEmail(email);
    }

    @Test
    void loadUserByUsername_whenUserNotFound_thenShouldThrowUsernameNotFoundException() {
        // given
        String email = "nonexistent@example.com";
        when(userService.findUserByEmail(email)).thenReturn(Optional.empty());

        // when
        // then
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(email);
        });
        assertTrue(thrown.getMessage().contains("User " + email + " not found"));
        verify(userService, times(1)).findUserByEmail(email);
    }

    @Test
    void loadUserByUsername_whenUserWithNoRoles_thenShouldReturnUserDetailsWithNoAuthorities() {
        // given
        String email = "no_roles@example.com";
        String password = "encodedPassword";

        UserLoginDTO mockUser = new UserLoginDTO(email, password, Collections.emptySet());

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(mockUser));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // then
        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().isEmpty());
    }
}
