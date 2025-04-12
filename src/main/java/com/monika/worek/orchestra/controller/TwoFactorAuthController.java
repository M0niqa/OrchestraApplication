package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.service.TwoFactorAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;

    public TwoFactorAuthController(TwoFactorAuthService twoFactorAuthService) {
        this.twoFactorAuthService = twoFactorAuthService;
    }

    @GetMapping("/2fa-page")
    public String twoFactorPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "2fa";
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendCode(@RequestParam String email) {
        twoFactorAuthService.sendVerificationCode(email);
        return ResponseEntity.ok("Verification code sent to email.");
    }

    @PostMapping("/2fa/verify")
    public String verify(@RequestParam String email,
                         @RequestParam String code,
                         Model model) {
        if (twoFactorAuthService.verifyCode(email, code)) {
            return "redirect:/musicianPage"; // or /adminPage
        } else {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid code. Please try again.");
            return "2fa";
        }
    }
}
