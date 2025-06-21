package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUsersController.class)
@WithMockUser(roles = "ADMIN")
class AdminUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void showAllUsers_whenGetRequest_thenShouldReturnViewWithUserList() throws Exception {
        // given
        UserBasicDTO userDto = new UserBasicDTO(1L, "John", "Smith", null);
        List<UserBasicDTO> userList = List.of(userDto);
        when(userService.getAllBasicDTOUsers()).thenReturn(userList);

        // when
        // then
        mockMvc.perform(get("/admin/allUsers"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-all-users"))
                .andExpect(model().attributeExists("allUsers"))
                .andExpect(model().attribute("allUsers", userList));
    }

    @Test
    void deleteUser_whenPostRequest_thenShouldDeleteUserAndRedirect() throws Exception {
        // given
        Long userId = 1L;
        doNothing().when(userService).deleteUserById(userId);

        // when
        // then
        mockMvc.perform(post("/admin/allUsers/{userId}/delete", userId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/allUsers"))
                .andExpect(flash().attribute("success", "User deleted successfully!"));

        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void addRole_whenPostRequest_thenShouldAddRoleAndRedirect() throws Exception {
        // given
        Long userId = 1L;
        String role = "INSPECTOR";
        UserBasicDTO userDto = new UserBasicDTO(userId, "John", "Smith", null);

        doNothing().when(userService).addRoleToUser(userId, role);
        when(userService.findUserById(userId)).thenReturn(userDto);

        // when
        // then
        mockMvc.perform(post("/admin/allUsers/{userId}/addRole", userId)
                        .param("role", role)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/allUsers"))
                .andExpect(flash().attribute("success", "John Smith set as inspector successfully!"));

        verify(userService, times(1)).addRoleToUser(userId, role);
    }

    @Test
    void removeRole_whenPostRequest_thenShouldRemoveRoleAndRedirect() throws Exception {
        // given
        Long userId = 1L;
        String role = "INSPECTOR";
        UserBasicDTO userDto = new UserBasicDTO(userId, "John", "Smith", null);

        doNothing().when(userService).removeRoleFromUser(userId, role);
        when(userService.findUserById(userId)).thenReturn(userDto);

        // when
        // then
        mockMvc.perform(post("/admin/allUsers/{userId}/removeRole", userId)
                        .param("role", role)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/allUsers"))
                .andExpect(flash().attribute("success", "John Smith removed from inspector role successfully!"));

        verify(userService, times(1)).removeRoleFromUser(userId, role);
    }
}
