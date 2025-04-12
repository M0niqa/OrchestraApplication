package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.service.MessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final MessageService messageService;

    public HomeController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("message", messageService.getMessage());
        return "login-form";
    }
//
//    @GetMapping("/userPage")
//    public String user(){
//        return "musician";
//    }
}
