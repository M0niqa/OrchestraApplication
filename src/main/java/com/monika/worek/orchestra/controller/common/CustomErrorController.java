package com.monika.worek.orchestra.controller.common;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = "An unexpected error occurred.";

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMessage = "Error 404: The page you are looking for does not exist.";
            } else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMessage = "Error 500: There was a problem with our server. Please try again later.";
            } else if(statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMessage = "Error 403: You do not have permission to access this page.";
            } else {
                errorMessage = "Error " + statusCode;
            }
        }

        model.addAttribute("message", errorMessage);
        return "common/error";
    }
}