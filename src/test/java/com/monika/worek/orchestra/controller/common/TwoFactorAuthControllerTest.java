package com.monika.worek.orchestra.controller.common;

import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.model.UserRole;
import com.monika.worek.orchestra.service.TwoFactorAuthService;
import com.monika.worek.orchestra.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TwoFactorAuthController.class)
class TwoFactorAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TwoFactorAuthService twoFactorAuthService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    void twoFactorPage_whenGetRequest_thenShouldReturn2faView() throws Exception {
        // given
        String email = "admin@example.com";

        // when
        // then
        mockMvc.perform(get("/2fa-page").param("email", email))
                .andExpect(status().isOk())
                .andExpect(view().name("common/2fa"))
                .andExpect(model().attribute("email", email));
    }

    @Test
    @WithMockUser
    void verify_whenCodeIsValidForAdmin_thenShouldRedirectToAdminPage() throws Exception {
        // given
        String email = "admin@example.com";
        String code = "123456";
        UserLoginDTO adminUser = new UserLoginDTO(email, null, Set.of(new UserRole(1L, "ADMIN")));

        when(twoFactorAuthService.isCodeValid(email, code)).thenReturn(true);
        when(userService.findUserByEmail(email)).thenReturn(Optional.of(adminUser));

        // when
        // then
        mockMvc.perform(post("/2fa/verify")
                        .with(csrf())
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/adminPage"));
    }

    @Test
    @WithMockUser
    void verify_whenCodeIsValidForInspector_thenShouldRedirectToInspectorPage() throws Exception {
        // given
        String email = "inspector@example.com";
        String code = "123456";
        UserLoginDTO inspectorUser = new UserLoginDTO(email, null, Set.of(new UserRole(2L, "INSPECTOR")));

        when(twoFactorAuthService.isCodeValid(email, code)).thenReturn(true);
        when(userService.findUserByEmail(email)).thenReturn(Optional.of(inspectorUser));

        // when
        // then
        mockMvc.perform(post("/2fa/verify")
                        .with(csrf())
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inspectorPage"));
    }

    @Test
    @WithMockUser
    void verify_whenCodeIsValidForMusician_thenShouldRedirectToMusicianPage() throws Exception {
        // given
        String email = "musician@example.com";
        String code = "123456";
        UserLoginDTO musicianUser = new UserLoginDTO(email, null, Set.of(new UserRole(3L, "MUSICIAN")));

        when(twoFactorAuthService.isCodeValid(email, code)).thenReturn(true);
        when(userService.findUserByEmail(email)).thenReturn(Optional.of(musicianUser));

        // when
        // then
        mockMvc.perform(post("/2fa/verify")
                        .with(csrf())
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/musicianPage"));
    }

    @Test
    @WithMockUser
    void verify_whenCodeIsInvalid_thenShouldReturnFormWithErrorMessage() throws Exception {
        // given
        String email = "user@example.com";
        String invalidCode = "654321";
        when(twoFactorAuthService.isCodeValid(email, invalidCode)).thenReturn(false);

        // when
        // then
        mockMvc.perform(post("/2fa/verify")
                        .with(csrf())
                        .param("email", email)
                        .param("code", invalidCode))
                .andExpect(status().isOk())
                .andExpect(view().name("common/2fa"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Invalid code. Please try again."));
    }
}
