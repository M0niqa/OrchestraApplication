package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.dto.MusicianDataDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MusicianDataController.class)
@WithMockUser(username = "user@example.com", roles = "MUSICIAN")
class MusicianDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MusicianService musicianService;

    @MockBean
    private UserService userService;

    private Musician currentUser;

    @BeforeEach
    void setUp() {
        currentUser = Musician.builder()
                .id(1L)
                .email("user@example.com")
                .build();
    }

    @Test
    void showUpdateDataForm_whenGetRequest_thenShouldReturnFormViewWithData() throws Exception {
        // given
        when(musicianService.getMusicianDtoByEmail("user@example.com")).thenReturn(new MusicianDataDTO());

        // when
        // then
        mockMvc.perform(get("/musicianData"))
                .andExpect(status().isOk())
                .andExpect(view().name("musician/musician-data"))
                .andExpect(model().attributeExists("musician", "taxOffices"));
    }

    @Test
    void updateData_whenEmailIsNotChanged_thenShouldUpdateDataAndRedirectToMusicianPage() throws Exception {
        // given
        when(musicianService.getMusicianByEmail("user@example.com")).thenReturn(currentUser);
        doNothing().when(musicianService).updateMusicianData(eq("user@example.com"), any(MusicianDataDTO.class));

        // when
        // then
        mockMvc.perform(post("/musicianData")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "John")
                        .param("lastName", "Smith")
                        .param("email", "user@example.com")
                        .param("birthdate", "1990-01-01")
                        .param("address", "123 Main St")
                        .param("pesel", "44051401458")
                        .param("bankAccountNumber", "12345")
                        .param("taxOffice", "US_KRAKOW_NOWA_HUTA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/musicianPage"))
                .andExpect(flash().attribute("success", "Data updated successfully!"));

        verify(musicianService, times(1)).updateMusicianData(anyString(), any(MusicianDataDTO.class));
    }

    @Test
    void updateData_whenEmailIsChangedSuccessfully_thenShouldUpdateAndRedirectToLogin() throws Exception {
        // given
        String newEmail = "new.email@example.com";
        when(musicianService.getMusicianByEmail("user@example.com")).thenReturn(currentUser);
        when(userService.doesUserExist(newEmail)).thenReturn(false);
        doNothing().when(musicianService).updateMusicianData(eq("user@example.com"), any(MusicianDataDTO.class));

        // when
        // then
        mockMvc.perform(post("/musicianData")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", newEmail)
                        .param("firstName", "John")
                        .param("lastName", "Smith")
                        .param("birthdate", "1990-01-01")
                        .param("address", "123 Main St")
                        .param("pesel", "44051401458")
                        .param("bankAccountNumber", "12345")
                        .param("taxOffice", "US_KRAKOW_NOWA_HUTA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?emailChanged"));
    }

    @Test
    void updateData_whenEmailIsChangedToExistingEmail_thenShouldReturnFormWithError() throws Exception {
        // given
        String existingEmail = "already.taken@example.com";
        when(musicianService.getMusicianByEmail("user@example.com")).thenReturn(currentUser);
        when(userService.doesUserExist(existingEmail)).thenReturn(true);

        // when
        // then
        mockMvc.perform(post("/musicianData")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", existingEmail)
                        .param("firstName", "John")
                        .param("lastName", "Smith")
                        .param("birthdate", "1990-01-01")
                        .param("address", "123 Main St")
                        .param("pesel", "44051401458")
                        .param("bankAccountNumber", "12345")
                        .param("taxOffice", "US_KRAKOW_NOWA_HUTA"))
                .andExpect(status().isOk())
                .andExpect(view().name("musician/musician-data"))
                .andExpect(model().attributeHasFieldErrors("musician", "email"));
    }

    @Test
    void updateData_whenDtoIsInvalid_thenShouldReturnFormWithErrors() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(post("/musicianData")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("musician/musician-data"))
                .andExpect(model().attributeHasFieldErrors("musician", "firstName"));
    }
}
