package com.monika.worek.orchestra.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // TODO: remove message

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException ex, Model model) {
        model.addAttribute("message", "File operation failed: " + ex.getMessage());
        return "error";
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        redirectAttributes.addFlashAttribute("error", "File is too large. Maximum allowed size is 5MB.");
        return "redirect:" + referer;
    }
}