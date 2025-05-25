package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.dto.MusicianDataDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.TaxOffice;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MusicianDataController {

    private final MusicianService musicianService;
    private final UserService userService;

    public MusicianDataController(MusicianService musicianService, UserService userService) {
        this.musicianService = musicianService;
        this.userService = userService;
    }

    @GetMapping("/musicianData")
    public String showUpdateDataForm(Model model, Authentication authentication) {
        String currentEmail = authentication.getName();
        MusicianDataDTO dto = musicianService.getMusicianDtoByEmail(currentEmail);
        System.out.println(dto.getInstrument());
        model.addAttribute("musician", musicianService.getMusicianDtoByEmail(currentEmail));
        model.addAttribute("taxOffices", TaxOffice.values());
        return "musician/musician-data";
    }

    @PostMapping("/musicianData")
    public String updateData(@Valid @ModelAttribute("musician") MusicianDataDTO dto, BindingResult bindingResult,
                             Authentication authentication, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("taxOffices", TaxOffice.values());
            return "musician/musician-data";
        }

        String currentEmail = authentication.getName();
        Musician musician = musicianService.getMusicianByEmail(currentEmail);

        boolean emailChanged = !musician.getEmail().equals(dto.getEmail());
        if (emailChanged) {
            if (userService.doesUserExist(dto.getEmail())) {
                bindingResult.rejectValue("email", "error.email", "User with this email already exists.");
                model.addAttribute("taxOffices", TaxOffice.values());
                return "musician/musician-data";
            }
            musicianService.updateMusicianData(authentication.getName(), dto);
            SecurityContextHolder.clearContext();
            return "redirect:/login?emailChanged";
        }

        musicianService.updateMusicianData(authentication.getName(), dto);
        redirectAttributes.addFlashAttribute("success", "Data updated successfully!");
        return "redirect:/musicianPage";
    }
}
