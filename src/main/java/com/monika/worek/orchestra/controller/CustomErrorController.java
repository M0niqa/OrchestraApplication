package com.monika.worek.orchestra.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController {

    @GetMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("message", "Unexpected error occurred.");
        return "error";
    }
}
