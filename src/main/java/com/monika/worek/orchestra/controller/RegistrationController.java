package com.monika.worek.orchestra.controller;//package com.monika.worek.orchestra.controller;
//
//import com.monika.worek.orchestra.dto.MusicianRegisterDTO;
//import com.monika.worek.orchestra.service.MusicianService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//
//@Controller
//public class RegistrationController {
//
//    private final MusicianService userService;
//
//    public RegistrationController(MusicianService userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping("/register")
//    public String register(Model model){
//        model.addAttribute("musician", new MusicianRegisterDTO());
//        return "registration-form";
//    }
//
//    @PostMapping("/register")
//    public String register(MusicianRegisterDTO user){
//        userService.register(user);
//        return "redirect:/confirm";
//    }
//
//    @GetMapping("/confirm")
//    public String confirm(){
//        return "registration-confirm";
//    }
//
//}
