package com.monika.worek.orchestra;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public String handleIOException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "File operation failed: ");
        return "redirect:/error";
    }
}