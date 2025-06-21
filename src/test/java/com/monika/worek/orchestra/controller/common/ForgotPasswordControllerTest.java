package com.monika.worek.orchestra.controller.common;

import com.monika.worek.orchestra.service.PasswordResetService;
import com.monika.worek.orchestra.service.TokenService;
import com.monika.worek.orchestra.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForgotPasswordController.class)
@AutoConfigureMockMvc(addFilters = false)
class ForgotPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;

    @Test
    void forgotPasswordPage_whenGetRequest_thenShouldReturnView() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("common/forgot-password"));
    }

    @Test
    void forgotPassword_whenPostRequest_thenShouldSendLinkAndRedirect() throws Exception {
        // given
        String email = "test@example.com";
        doNothing().when(passwordResetService).sendResetLink(email);

        // when
        // then
        mockMvc.perform(post("/forgot-password")
                        .param("email", email)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password?sendSuccess"));

        verify(passwordResetService, times(1)).sendResetLink(email);
    }

    @Test
    void resetPasswordPage_whenTokenIsValid_thenShouldReturnResetPasswordView() throws Exception {
        // given
        String validToken = "valid-token";
        when(tokenService.getEmailForToken(validToken)).thenReturn("user@example.com");

        // when
        // then
        mockMvc.perform(get("/reset-password").param("token", validToken))
                .andExpect(status().isOk())
                .andExpect(view().name("common/reset-password"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @Test
    void resetPasswordPage_whenTokenIsInvalid_thenShouldReturnTokenInvalidView() throws Exception {
        // given
        String invalidToken = "invalid-token";
        when(tokenService.getEmailForToken(invalidToken)).thenReturn(null);

        // when
        // then
        mockMvc.perform(get("/reset-password").param("token", invalidToken))
                .andExpect(status().isOk())
                .andExpect(view().name("common/token-invalid"));
    }

    @Test
    void resetPassword_whenPasswordsMatchAndTokenIsValid_thenShouldUpdatePasswordAndRedirect() throws Exception {
        // given
        String validToken = "valid-token";
        String email = "user@example.com";
        when(tokenService.getEmailForToken(validToken)).thenReturn(email);
        doNothing().when(userService).updatePassword(anyString(), anyString());
        doNothing().when(tokenService).invalidateToken(validToken);

        // when
        // then
        mockMvc.perform(post("/reset-password")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", validToken)
                        .param("newPassword", "NewPassword123!")
                        .param("confirmNewPassword", "NewPassword123!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?resetSuccess"));

        verify(userService, times(1)).updatePassword(email, "NewPassword123!");
        verify(tokenService, times(1)).invalidateToken(validToken);
    }

    @Test
    void resetPassword_whenPasswordsDoNotMatch_thenShouldReturnFormWithErrors() throws Exception {
        // given
        String validToken = "valid-token";
        when(tokenService.getEmailForToken(validToken)).thenReturn("user@example.com");
        // when
        // then
        mockMvc.perform(post("/reset-password")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", validToken)
                        .param("newPassword", "n0*(34OMOP")
                        .param("confirmNewPassword", "n0*(34OMOB")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("common/reset-password"))
                .andExpect(model().attributeHasFieldErrors("passwordForm", "confirmNewPassword"));
    }

    @Test
    void resetPassword_whenPasswordsDoNotComplyWithPattern_thenShouldReturnFormWithErrors() throws Exception {
        // given
        String validToken = "valid-token";
        when(tokenService.getEmailForToken(validToken)).thenReturn("user@example.com");
        // when
        // then
        mockMvc.perform(post("/reset-password")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", validToken)
                        .param("newPassword", "easyPassword")
                        .param("confirmNewPassword", "easyPassword")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("common/reset-password"))
                .andExpect(model().attributeHasFieldErrors("passwordForm", "newPassword"));
    }

    @Test
    void resetPassword_whenDtoIsInvalid_thenShouldReturnFormWithErrors() throws Exception {
        // given
        String validToken = "valid-token";

        // when
        // then
        mockMvc.perform(post("/reset-password")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", validToken)
                        .param("newPassword", "short")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("common/reset-password"))
                .andExpect(model().attributeHasFieldErrors("passwordForm", "newPassword"));
    }
}