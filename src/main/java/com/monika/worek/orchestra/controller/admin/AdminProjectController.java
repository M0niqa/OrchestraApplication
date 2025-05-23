package com.monika.worek.orchestra.controller.admin;


import com.monika.worek.orchestra.dtoMappers.ProjectBasicInfoDTOMapper;
import com.monika.worek.orchestra.dto.InstrumentCountAndSalaryDTO;
import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class AdminProjectController {

    private final ProjectService projectService;

    public AdminProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/admin/addProject")
    public String showAddProjectForm(Model model) {
        model.addAttribute("projectDTO", new ProjectBasicInfoDTO());
        return "/admin/add-project";
    }

    @PostMapping("/admin/addProject")
    public String addProject(@Valid @ModelAttribute("projectDTO") ProjectBasicInfoDTO projectDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "/admin/add-project";
        }
        Project project = ProjectBasicInfoDTOMapper.mapToEntity(projectDTO);
        projectService.saveProject(project);
        redirectAttributes.addFlashAttribute("success", "Project added successfully!");
        return "redirect:/admin/addProject";
    }

    @PostMapping("/admin/project/{id}/delete")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProjectById(id);
        return "redirect:/adminPage";
    }

    @GetMapping("/admin/project/{projectId}")
    public String viewProject(@PathVariable Long projectId, Model model) {
        ProjectBasicInfoDTO projectBasicDTO = projectService.getProjectBasicDtoById(projectId);
        model.addAttribute("project", projectBasicDTO);
        return "admin/admin-project-details";
    }

    @PostMapping("/admin/project/{projectId}/update")
    public String updateProject(@PathVariable Long projectId, @Valid @ModelAttribute("project") ProjectBasicInfoDTO projectBasicDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/admin-project-details";
        }
        projectService.updateBasicProjectInfo(projectId, projectBasicDTO);
        redirectAttributes.addFlashAttribute("success", "Project updated successfully!");
        return "redirect:/admin/project/" + projectId;
    }

    @GetMapping("/admin/project/{projectId}/instrumentCount")
    public String showInstrumentConfigForm(@PathVariable Long projectId, Model model) {
        Project project = projectService.getProjectById(projectId);

        InstrumentCountAndSalaryDTO instrCountAndSalaryDTO = new InstrumentCountAndSalaryDTO();
        instrCountAndSalaryDTO.setInstrumentCounts(project.getInstrumentCounts());
        instrCountAndSalaryDTO.setGroupSalaries(project.getGroupSalaries());

        model.addAttribute("projectId", projectId);
        model.addAttribute("instruments", Instrument.values());
        model.addAttribute("instrumentGroups", List.of("Strings", "Winds", "Brass", "Solo"));
        model.addAttribute("configDTO", instrCountAndSalaryDTO);
        return "admin/instrument-count";
    }

    @PostMapping("/admin/project/{projectId}/instrumentCount")
    public String updateInstrumentConfig(@PathVariable Long projectId, @ModelAttribute InstrumentCountAndSalaryDTO instrumentConfigDTO, RedirectAttributes redirectAttributes) {
        Project project = projectService.getProjectById(projectId);

        project.setInstrumentCounts(instrumentConfigDTO.getInstrumentCounts());
        project.setGroupSalaries(instrumentConfigDTO.getGroupSalaries());

        projectService.saveProject(project);

        redirectAttributes.addFlashAttribute("success", "Instrument configuration updated successfully!");
        return "redirect:/admin/project/" + projectId + "/instrumentCount";
    }
}
