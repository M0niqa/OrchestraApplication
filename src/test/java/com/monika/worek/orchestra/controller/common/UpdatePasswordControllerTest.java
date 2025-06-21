package com.monika.worek.orchestra.controller.common;

import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UpdatePasswordController.class)
@WithMockUser(username = "user@example.com", roles = "MUSICIAN")
class UpdatePasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserLoginDTO currentUser;

    @BeforeEach
    void setUp() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        currentUser = new UserLoginDTO(
                "user@example.com",
                passwordEncoder.encode("oldPassword123"),
                null
        );
    }

    @Test
    void showUpdatePasswordForm_whenGetRequest_thenShouldReturnView() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/userPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("common/update-password"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @Test
    void updatePassword_whenAllDataIsValid_thenShouldUpdatePasswordAndRedirect() throws Exception {
        // given
        when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.of(currentUser));

        // when
        // then
        mockMvc.perform(post("/userPassword")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("oldPassword", "oldPassword123")
                        .param("newPassword", "newStrongPassword456!")
                        .param("confirmNewPassword", "newStrongPassword456!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/userPassword"))
                .andExpect(flash().attribute("success", "Password updated successfully!"));

        verify(userService, times(1)).updatePassword("user@example.com", "newStrongPassword456!");
    }

    @Test
    void updatePassword_whenDtoIsInvalid_thenShouldReturnFormWithErrors() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(post("/userPassword")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("oldPassword", "oldPassword123")
                        .param("newPassword", "short")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("common/update-password"))
                .andExpect(model().attributeHasFieldErrors("passwordForm", "newPassword"));
    }

    @Test
    void updatePassword_whenOldPasswordIsIncorrect_thenShouldReturnFormWithErrors() throws Exception {
        // given
        when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.of(currentUser));

        // when
        // then
        mockMvc.perform(post("/userPassword")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("oldPassword", "wrongOldPassword")
                        .param("newPassword", "newStrongPassword456!")
                        .param("confirmNewPassword", "newStrongPassword456!")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("common/update-password"))
                .andExpect(model().attributeHasFieldErrorCode("passwordForm", "oldPassword", "error.oldPassword"));
    }

    @Test
    void updatePassword_whenNewPasswordsDoNotMatch_thenShouldReturnFormWithErrors() throws Exception {
        // given
        when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.of(currentUser));

        // when
        // then
        mockMvc.perform(post("/userPassword")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("oldPassword", "oldPassword123")
                        .param("newPassword", "05%$newPasswordA")
                        .param("confirmNewPassword", "05%$newPasswordB")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("common/update-password"))
                .andExpect(model().attributeHasFieldErrorCode("passwordForm", "confirmNewPassword", "error.confirmNewPassword"));
    }
}
