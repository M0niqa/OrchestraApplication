package com.monika.worek.orchestra.controller.inspector;

import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.dto.ProjectDTO;
import com.monika.worek.orchestra.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InspectorPageController.class)
@WithMockUser(username = "inspector@example.com", roles = "INSPECTOR")
class InspectorPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    void showInspectorPage_whenGetRequest_thenShouldReturnViewWithProjects() throws Exception {
        // given
        List<ProjectBasicInfoDTO> ongoingProjects = List.of(new ProjectBasicInfoDTO());
        List<ProjectBasicInfoDTO> futureProjects = List.of(new ProjectBasicInfoDTO());

        when(projectService.getOngoingProjectsDTOs()).thenReturn(ongoingProjects);
        when(projectService.getFutureProjectsDTOs()).thenReturn(futureProjects);

        // when
        // then
        mockMvc.perform(get("/inspectorPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("/inspector/inspector-main-page"))
                .andExpect(model().attribute("ongoingProjects", ongoingProjects))
                .andExpect(model().attribute("futureProjects", futureProjects));
    }

    @Test
    void showArchivedProjects_whenRequestingAsInspector_thenShouldReturnViewWithArchivedProjects() throws Exception {
        // given
        List<ProjectBasicInfoDTO> archivedProjects = List.of(new ProjectBasicInfoDTO());
        when(projectService.getArchivedProjectsDTOs()).thenReturn(archivedProjects);

        // when
        // then
        mockMvc.perform(get("/inspector/archived"))
                .andExpect(status().isOk())
                .andExpect(view().name("inspector/archived-projects"))
                .andExpect(model().attribute("archivedProjects", archivedProjects));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showArchivedProjects_whenRequestingAsAdmin_thenShouldReturnViewWithArchivedProjects() throws Exception {
        // given
        List<ProjectBasicInfoDTO> archivedProjects = List.of(new ProjectBasicInfoDTO());
        when(projectService.getArchivedProjectsDTOs()).thenReturn(archivedProjects);

        // when
        // then
        mockMvc.perform(get("/admin/archived"))
                .andExpect(status().isOk())
                .andExpect(view().name("inspector/archived-projects"))
                .andExpect(model().attribute("archivedProjects", archivedProjects));
    }

    @Test
    void viewArchivedProjectDetails_whenRequestingAsInspector_thenShouldReturnDetailsView() throws Exception {
        // given
        Long projectId = 1L;
        ProjectDTO projectDto = ProjectDTO.builder().id(projectId).build();
        when(projectService.getProjectDtoById(projectId)).thenReturn(projectDto);

        // when
        // then
        mockMvc.perform(get("/inspector/archived/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("inspector/archived-project-details"))
                .andExpect(model().attribute("project", projectDto));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void viewArchivedProjectDetails_whenRequestingAsAdmin_thenShouldReturnDetailsView() throws Exception {
        // given
        Long projectId = 1L;
        ProjectDTO projectDto = ProjectDTO.builder().id(projectId).build();
        when(projectService.getProjectDtoById(projectId)).thenReturn(projectDto);

        // when
        // then
        mockMvc.perform(get("/admin/archived/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("inspector/archived-project-details"))
                .andExpect(model().attribute("project", projectDto));
    }
}
