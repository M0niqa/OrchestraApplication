package com.monika.worek.orchestra.controller.inspector;

import com.monika.worek.orchestra.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterMusicianController.class)
@WithMockUser(username = "inspector@example.com", roles = "INSPECTOR")
class RegisterMusicianControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @Test
    void showRegisterMusician_whenGetRequest_thenShouldReturnRegistrationForm() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/inspector/registerMusician"))
                .andExpect(status().isOk())
                .andExpect(view().name("/inspector/registration-form"))
                .andExpect(model().attributeExists("musician", "instruments"));
    }

    @Test
    void registerMusician_whenDataIsValid_thenShouldCreateUserAndRedirectWithSuccess() throws Exception {
        // given
        doNothing().when(registrationService).createMusician(any());
        doNothing().when(registrationService).sendLink(anyString());

        // when
        // then
        mockMvc.perform(post("/inspector/registerMusician")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "John")
                        .param("lastName", "Smith")
                        .param("email", "john.doe@example.com")
                        .param("instrument", "VIOLIN_I")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inspector/registerMusician"))
                .andExpect(flash().attribute("success", "Musician registered successfully!"));

        verify(registrationService, times(1)).createMusician(any());
        verify(registrationService, times(1)).sendLink("john.doe@example.com");
    }

    @Test
    void registerMusician_whenDataIsInvalid_thenShouldReturnFormWithErrors() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(post("/inspector/registerMusician")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "")
                        .param("lastName", "Smith")
                        .param("email", "john.doe@example.com")
                        .param("instrument", "VIOLIN_I")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/inspector/registration-form"))
                .andExpect(model().attributeHasFieldErrors("musician", "firstName"));

        verify(registrationService, never()).createMusician(any());
    }

    @Test
    void registerMusician_whenMusicianAlreadyExists_thenShouldReturnFormWithError() throws Exception {
        // given
        doThrow(new IllegalArgumentException("Musician already exists."))
                .when(registrationService).createMusician(any());

        // when
        // then
        mockMvc.perform(post("/inspector/registerMusician")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "John")
                        .param("lastName", "Smith")
                        .param("email", "existing@example.com")
                        .param("instrument", "VIOLIN_I")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/inspector/registration-form"))
                .andExpect(model().attribute("error", "User with this email already exists."));

        verify(registrationService, times(1)).createMusician(any());
        verify(registrationService, never()).sendLink(anyString());
    }
}