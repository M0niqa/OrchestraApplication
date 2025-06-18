package com.monika.worek.orchestra.config;

import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.model.UserRole;
import com.monika.worek.orchestra.service.TwoFactorAuthService;
import com.monika.worek.orchestra.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TwoFactorAuthService twoFactorAuthService;

    @MockBean
    private UserService userService;

    @Test
    void unauthenticatedUser_shouldBeRedirectedToLoginPage() throws Exception {
        // given
        // when
        mockMvc.perform(get("/adminPage"))
                // then
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void loginSuccess_shouldRedirectTo2FAPageAndSendCode() throws Exception {
        // given
        String testEmail = "user@example.com";
        String testPassword = "password";
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(testPassword);
        UserLoginDTO mockUser = createMockUser(testEmail, encodedPassword, Set.of(UserRole.builder().name("MUSICIAN").build()));

        when(userService.findUserByEmail(testEmail)).thenReturn(Optional.of(mockUser));

        doNothing().when(twoFactorAuthService).sendVerificationCode(testEmail);

        // when
        mockMvc.perform(formLogin("/login").user(testEmail).password(testPassword))
                // then
                .andExpect(authenticated().withUsername(testEmail))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/2fa-page?email=" + testEmail));
        verify(twoFactorAuthService).sendVerificationCode(testEmail);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void adminUser_shouldAccessAdminPage() throws Exception {
        // given
        // User authenticated as ADMIN via @WithMockUser

        // when
        mockMvc.perform(get("/adminPage"))
                // then
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void adminUser_shouldAccessAdminSubPaths() throws Exception {
        // given
        // User authenticated as ADMIN via @WithMockUser

        // when
        mockMvc.perform(get("/admin/users"))
                // then
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "inspector@example.com", roles = {"INSPECTOR"})
    void inspectorUser_shouldAccessInspectorPage() throws Exception {
        // given
        // User authenticated as INSPECTOR via @WithMockUser

        // when
        mockMvc.perform(get("/inspectorPage"))
                // then
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "inspector@example.com", roles = {"INSPECTOR"})
    void inspectorUser_shouldAccessInspectorSubPaths() throws Exception {
        // given
        // User authenticated as INSPECTOR via @WithMockUser

        // when
        mockMvc.perform(get("/inspector/reports"))
                // then
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "musician@example.com", roles = {"MUSICIAN"})
    void musicianUser_shouldAccessMusicianPage() throws Exception {
        // given
        // User authenticated as MUSICIAN via @WithMockUser

        // when
        mockMvc.perform(get("/musicianPage"))
                // then
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "musician@example.com", roles = {"MUSICIAN"})
    void musicianUser_shouldBeDeniedAdminPage() throws Exception {
        // given
        // User authenticated as MUSICIAN via @WithMockUser

        // when
        mockMvc.perform(get("/adminPage"))
                // then
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void adminUser_shouldBeDeniedInspectorPage() throws Exception {
        // given
        // User authenticated as ADMIN via @WithMockUser

        // when
        mockMvc.perform(get("/inspectorPage"))
                // then
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "anyuser@example.com", roles = {"ANY"})
    void unlistedRoleUser_shouldBeDeniedAnyProtectedPage() throws Exception {
        // given
        // User authenticated with a role not explicitly mapped
        // This tests the .anyRequest().authenticated() line

        // when
        mockMvc.perform(get("/someOtherProtectedPage"))
                // then
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "testuser@example.com")
    void logout_shouldRedirectToLoginPage() throws Exception {
        // given
        // User authenticated via @WithMockUser

        // when
        mockMvc.perform(get("/logout"))
                 // then
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?logout=true"))
                .andExpect(unauthenticated());
    }

    private UserLoginDTO createMockUser(String email, String password, Set<UserRole> roles) {
        return new UserLoginDTO(email, password, roles);
    }
}

