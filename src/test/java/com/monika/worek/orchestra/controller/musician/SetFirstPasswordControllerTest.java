package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.service.TokenService;
import com.monika.worek.orchestra.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SetFirstPasswordController.class)
@WithMockUser
class SetFirstPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserService userService;

    @Test
    void setPasswordPage_whenTokenIsValid_thenShouldReturnSetPasswordView() throws Exception {
        // given
        String validToken = "valid-token";
        when(tokenService.getEmailForToken(validToken)).thenReturn("user@example.com");

        // when
        // then
        mockMvc.perform(get("/set-password").param("token", validToken))
                .andExpect(status().isOk())
                .andExpect(view().name("common/set-password"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @Test
    void setPasswordPage_whenTokenIsInvalid_thenShouldReturnTokenInvalidView() throws Exception {
        // given
        String invalidToken = "invalid-token";
        when(tokenService.getEmailForToken(invalidToken)).thenReturn(null);

        // when
        // then
        mockMvc.perform(get("/set-password").param("token", invalidToken))
                .andExpect(status().isOk())
                .andExpect(view().name("common/token-invalid"));
    }

    @Test
    void setPassword_whenFormIsValidAndTokenIsValid_thenShouldUpdatePasswordAndRedirect() throws Exception {
        // given
        String validToken = "valid-token";
        String email = "user@example.com";
        when(tokenService.getEmailForToken(validToken)).thenReturn(email);
        doNothing().when(userService).updatePassword(anyString(), anyString());
        doNothing().when(tokenService).invalidateToken(validToken);

        // when
        // then
        mockMvc.perform(post("/set-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", validToken)
                        .param("newPassword", "NewPassword123!")
                        .param("confirmNewPassword", "NewPassword123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?setSuccess"));

        verify(userService, times(1)).updatePassword(email, "NewPassword123!");
        verify(tokenService, times(1)).invalidateToken(validToken);
    }

    @Test
    void setPassword_whenPasswordsDoNotMatch_thenShouldReturnFormWithErrors() throws Exception {
        // given
        String validToken = "valid-token";
        when(tokenService.getEmailForToken(validToken)).thenReturn("user@example.com");

        // when
        // then
        mockMvc.perform(post("/set-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", validToken)
                        .param("newPassword", "04$%PasswordA")
                        .param("confirmNewPassword", "04$%PasswordB"))
                .andExpect(status().isOk())
                .andExpect(view().name("common/set-password"))
                .andExpect(model().attributeHasFieldErrorCode("passwordForm", "confirmNewPassword", "error.confirmNewPassword"));
    }

    @Test
    void setPassword_whenDtoIsInvalid_thenShouldReturnFormWithErrors() throws Exception {
        // given
        String validToken = "valid-token";

        // when
        // then
        mockMvc.perform(post("/set-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", validToken)
                        .param("newPassword", "short")) // Invalid password
                .andExpect(status().isOk())
                .andExpect(view().name("common/set-password"))
                .andExpect(model().attributeHasFieldErrors("passwordForm", "newPassword"));
    }
}
