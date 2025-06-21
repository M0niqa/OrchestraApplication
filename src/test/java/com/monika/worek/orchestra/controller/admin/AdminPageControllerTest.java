package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.service.ChatService;
import com.monika.worek.orchestra.service.ProjectService;
import com.monika.worek.orchestra.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminPageController.class)
class AdminPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    @MockBean
    private ChatService chatService;

    @Test
    void showAdminPage_whenUserIsAuthenticated_thenReturnsAdminPageWithModelAttributes() throws Exception {
        // given
        String adminEmail = "admin@example.com";
        UserBasicDTO adminDto = new UserBasicDTO(1L, "Admin", "User", null);
        List<ProjectBasicInfoDTO> ongoingProjects = List.of(new ProjectBasicInfoDTO());
        List<ProjectBasicInfoDTO> futureProjects = List.of(new ProjectBasicInfoDTO());

        when(userService.getUserBasicDtoByEmail(adminEmail)).thenReturn(adminDto);
        when(chatService.areUnreadMessages(1L)).thenReturn(true);
        when(projectService.getOngoingProjectsDTOs()).thenReturn(ongoingProjects);
        when(projectService.getFutureProjectsDTOs()).thenReturn(futureProjects);

        // when
        // then
        mockMvc.perform(get("/adminPage")
                        .with(user(adminEmail)))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/admin-main-page"))
                .andExpect(model().attributeExists("unreads", "userId", "ongoingProjects", "futureProjects"))
                .andExpect(model().attribute("unreads", true))
                .andExpect(model().attribute("userId", 1L))
                .andExpect(model().attribute("ongoingProjects", ongoingProjects))
                .andExpect(model().attribute("futureProjects", futureProjects));
    }
}
