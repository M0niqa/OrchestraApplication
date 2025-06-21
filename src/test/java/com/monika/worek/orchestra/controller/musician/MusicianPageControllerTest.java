package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.dto.MusicianBasicDTO;
import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.service.ChatService;
import com.monika.worek.orchestra.service.MusicianService;
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

@WebMvcTest(MusicianPageController.class)
@WithMockUser(username = "musician@example.com", roles = "MUSICIAN")
class MusicianPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MusicianService musicianService;

    @MockBean
    private ChatService chatService;

    @MockBean
    private ProjectService projectService;

    @Test
    void showMusicianPage_whenGetRequest_thenShouldReturnViewWithAllData() throws Exception {
        // given
        String email = "musician@example.com";
        long musicianId = 1L;
        MusicianBasicDTO musicianDTO = new MusicianBasicDTO(musicianId, "John", "Doe", null);
        List<ProjectBasicInfoDTO> projectList = List.of(new ProjectBasicInfoDTO());

        when(musicianService.getMusicianBasicDtoByEmail(email)).thenReturn(musicianDTO);
        when(projectService.getFutureProjectsDTOsByMusicianId(musicianId)).thenReturn(projectList);
        when(chatService.areUnreadMessages(musicianId)).thenReturn(true);
        when(musicianService.getActivePendingProjectsDTOs(musicianId)).thenReturn(projectList);
        when(musicianService.getActiveAcceptedProjectsDTOs(musicianId)).thenReturn(projectList);
        when(musicianService.getActiveRejectedProjectsDTOs(musicianId)).thenReturn(projectList);

        // when
        // then
        mockMvc.perform(get("/musicianPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("musician/musician-home-page"))
                .andExpect(model().attributeExists(
                        "musicianId",
                        "unreads",
                        "musician",
                        "pendingProjects",
                        "acceptedProjects",
                        "rejectedProjects",
                        "futureProjects"
                ))
                .andExpect(model().attribute("unreads", true))
                .andExpect(model().attribute("musician", musicianDTO));
    }

    @Test
    void showArchivedProjects_whenGetRequest_thenShouldReturnViewWithArchivedData() throws Exception {
        // given
        long musicianId = 1L;
        List<ProjectBasicInfoDTO> archivedAccepted = List.of(new ProjectBasicInfoDTO());
        List<ProjectBasicInfoDTO> archivedRejected = List.of(new ProjectBasicInfoDTO());

        when(musicianService.getArchivedAcceptedProjectsDTOs(musicianId)).thenReturn(archivedAccepted);
        when(musicianService.getArchivedRejectedProjectsDTOs(musicianId)).thenReturn(archivedRejected);

        // when
        // then
        mockMvc.perform(get("/musician/{musicianId}/archivedProjects", musicianId))
                .andExpect(status().isOk())
                .andExpect(view().name("musician/musician-archived-projects"))
                .andExpect(model().attribute("archivedAcceptedProjects", archivedAccepted))
                .andExpect(model().attribute("archivedRejectedProjects", archivedRejected));
    }
}