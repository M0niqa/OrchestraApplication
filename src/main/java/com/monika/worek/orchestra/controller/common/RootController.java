package com.monika.worek.orchestra.controller.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String handleRootRedirect() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return "redirect:/adminPage";
            }
            if ("ROLE_INSPECTOR".equals(authority.getAuthority())) {
                return "redirect:/inspectorPage";
            }
            if ("ROLE_MUSICIAN".equals(authority.getAuthority())) {
                return "redirect:/musicianPage";
            }
        }

        return "redirect:/error";
    }
}