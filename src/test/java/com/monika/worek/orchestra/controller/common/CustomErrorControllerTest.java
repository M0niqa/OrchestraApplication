package com.monika.worek.orchestra.controller.common;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomErrorController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleError_whenStatusIs404_thenShouldReturnErrorPageWithNotFoundMessage() throws Exception {
        // given
        MockHttpServletRequestBuilder errorRequest = get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value());

        // when
        // then
        mockMvc.perform(errorRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("common/error"))
                .andExpect(model().attribute("message", "Error 404: The page you are looking for does not exist."));
    }

    @Test
    void handleError_whenStatusIs500_thenShouldReturnErrorPageWithInternalServerErrorMessage() throws Exception {
        // given
        MockHttpServletRequestBuilder errorRequest = get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());

        // when
        // then
        mockMvc.perform(errorRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("common/error"))
                .andExpect(model().attribute("message", "Error 500: There was a problem with our server. Please try again later."));
    }

    @Test
    void handleError_whenStatusIs403_thenShouldReturnErrorPageWithForbiddenMessage() throws Exception {
        // given
        MockHttpServletRequestBuilder errorRequest = get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.FORBIDDEN.value());

        // when
        // then
        mockMvc.perform(errorRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("common/error"))
                .andExpect(model().attribute("message", "Error 403: You do not have permission to access this page."));
    }

    @Test
    void handleError_whenStatusIsOtherCode_thenShouldReturnErrorPageWithGenericStatusCodeMessage() throws Exception {
        // given
        int otherStatusCode = 418;
        MockHttpServletRequestBuilder errorRequest = get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, otherStatusCode);

        // when
        // then
        mockMvc.perform(errorRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("common/error"))
                .andExpect(model().attribute("message", "Error " + otherStatusCode));
    }

    @Test
    void handleError_whenStatusIsNull_thenShouldReturnErrorPageWithDefaultMessage() throws Exception {
        // given
        MockHttpServletRequestBuilder errorRequest = get("/error");

        // when
        // then
        mockMvc.perform(errorRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("common/error"))
                .andExpect(model().attribute("message", "An unexpected error occurred."));
    }
}
