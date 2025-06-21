package com.monika.worek.orchestra.controller.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RootController.class)
class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void handleRootRedirect_whenUserIsAdmin_thenShouldRedirectToAdminPage() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/adminPage"));
    }

    @Test
    @WithMockUser(roles = "INSPECTOR")
    void handleRootRedirect_whenUserIsInspector_thenShouldRedirectToInspectorPage() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inspectorPage"));
    }

    @Test
    @WithMockUser(roles = "MUSICIAN")
    void handleRootRedirect_whenUserIsMusician_thenShouldRedirectToMusicianPage() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/musicianPage"));
    }

    @Test
    @WithMockUser(roles = "UNKNOWN_ROLE")
    void handleRootRedirect_whenUserHasUnknownRole_thenShouldRedirectToErrorPage() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));
    }
}