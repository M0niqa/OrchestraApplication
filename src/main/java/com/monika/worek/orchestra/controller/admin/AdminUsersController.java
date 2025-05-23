package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminUsersController {

    private final UserService userService;

    public AdminUsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/allUsers")
    public String showAllUsers(Model model) {
        List<UserBasicDTO> usersDTO = userService.getAllBasicDTOUsers();
        model.addAttribute("allUsers", usersDTO);
        return "admin/admin-all-users";
    }

    @PostMapping("/admin/allUsers/{userId}/delete")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(userId);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        return "redirect:/admin/allUsers";
    }

    @PostMapping("/admin/allUsers/{userId}/addRole")
    public String addRole(@PathVariable Long userId,
                          @RequestParam String role,
                          RedirectAttributes redirectAttributes) {
        userService.addRoleToUser(userId, role);
        UserBasicDTO user = userService.findUserById(userId);
        redirectAttributes.addFlashAttribute("success",
                user.getFirstName() + " " + user.getLastName() + " set as inspector successfully!");
        return "redirect:/admin/allUsers";
    }

    @PostMapping("/admin/allUsers/{userId}/removeRole")
    public String removeRole(@PathVariable Long userId,
                             @RequestParam String role,
                             RedirectAttributes redirectAttributes) {
        userService.removeRoleFromUser(userId, role);
        UserBasicDTO user = userService.findUserById(userId);
        redirectAttributes.addFlashAttribute("success",
                user.getFirstName() + " " + user.getLastName() + " removed from inspector role successfully!");
        return "redirect:/admin/allUsers";
    }
}
