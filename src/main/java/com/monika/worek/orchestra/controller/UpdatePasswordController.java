package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.PasswordUpdateDTO;
import com.monika.worek.orchestra.dto.UserLoginDTO;
import com.monika.worek.orchestra.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UpdatePasswordController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UpdatePasswordController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/userPassword")
    public String showUpdatePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordUpdateDTO());
        return "update-password";
    }

    @PostMapping("/userPassword")
    public String updatePassword(@Valid @ModelAttribute("passwordForm") PasswordUpdateDTO form, BindingResult bindingResult,
                                 Authentication authentication, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "update-password";
        }

        String currentEmail = authentication.getName();
        UserLoginDTO user = userService.findUserByEmail(currentEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        String storedPassword = user.getPassword();

        if (!passwordEncoder.matches(form.getOldPassword(), storedPassword)) {
            bindingResult.rejectValue("oldPassword", "error.oldPassword", "Old password is incorrect");
            return "update-password";
        }

        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "error.confirmNewPassword", "Passwords do not match");
            return "update-password";
        }

        userService.updatePassword(currentEmail, form.getNewPassword());
        redirectAttributes.addFlashAttribute("success", "Password updated successfully!");
        return "redirect:/userPassword";
    }
}
