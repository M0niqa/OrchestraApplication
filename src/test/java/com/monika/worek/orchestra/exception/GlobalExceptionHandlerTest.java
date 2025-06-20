package com.monika.worek.orchestra.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// We tell Spring to load only our exception handler and the dummy controller for this test
@WebMvcTest(controllers = {GlobalExceptionHandler.class, GlobalExceptionHandlerTest.DummyController.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // A simple controller created only for throwing exceptions in our tests
    @Controller
    static class DummyController {
        @GetMapping("/throw-generic")
        public void throwGenericException() {
            throw new RuntimeException("A generic error occurred");
        }

        @GetMapping("/throw-io")
        public void throwIOException() throws IOException {
            throw new IOException("Cannot read file");
        }

        @PostMapping("/throw-max-size")
        public void throwMaxSizeException() {
            throw new MaxUploadSizeExceededException(5 * 1024 * 1024);
        }
    }

    @Test
    void handleException_whenGenericExceptionIsThrown_thenReturnsErrorPage() throws Exception {
        // given: The /throw-generic endpoint will throw a RuntimeException

        // when & then
        mockMvc.perform(get("/throw-generic"))
                .andExpect(status().isOk()) // Expect HTTP 200 because the handler returns a view
                .andExpect(view().name("error"))
                .andExpect(model().attribute("message", "A generic error occurred"));
    }

    @Test
    void handleIOException_whenIOExceptionIsThrown_thenReturnsErrorPageWithCustomMessage() throws Exception {
        // given: The /throw-io endpoint will throw an IOException

        // when & then
        mockMvc.perform(get("/throw-io"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("message", "File operation failed: Cannot read file"));
    }

    @Test
    void handleMaxSizeException_whenFileIsTooLarge_thenRedirectsWithFlashAttribute() throws Exception {
        // given: The /throw-max-size endpoint will throw MaxUploadSizeExceededException
        String previousPage = "/upload-form";

        // when & then
        mockMvc.perform(post("/throw-max-size").header("Referer", previousPage))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(previousPage))
                .andExpect(flash().attribute("error", "File is too large. Maximum allowed size is 5MB."));
    }
}