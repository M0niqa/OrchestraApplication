package com.monika.worek.orchestra.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adminPage")
public class AdminController {

    @GetMapping
    public String admin(){
        return "admin";
    }
}
