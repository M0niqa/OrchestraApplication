package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.service.TwoFactorAuthService;
import com.monika.worek.orchestra.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;
    private final UserService userService;

    public TwoFactorAuthController(TwoFactorAuthService twoFactorAuthService, UserService userService) {
        this.twoFactorAuthService = twoFactorAuthService;
        this.userService = userService;
    }

    @GetMapping("/2fa-page")
    public String twoFactorPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "2fa";
    }

    @PostMapping("/2fa/verify")
    public String verify(@RequestParam String email, @RequestParam String code, Model model) {
        if (twoFactorAuthService.isCodeValid(email, code)) {
            UserLoginDTO user = userService.findUserByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            boolean isAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ADMIN"));
            boolean isInspector = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("INSPECTOR"));
            boolean isMusician = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("MUSICIAN"));

            if (isAdmin) {
                return "redirect:/adminPage";
            }

            model.addAttribute("isInspector", isInspector);
            model.addAttribute("isMusician", isMusician);

            if (isInspector) {
                return "redirect:/inspectorPage";
            } else if (isMusician) {
                return "redirect:/musicianPage";
            } else {
                throw new IllegalStateException("No valid role assigned.");
            }
        } else {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid code. Please try again.");
            return "2fa";
        }
    }
}
