package com.monika.worek.orchestra.controller.admin;

import com.monika.worek.orchestra.dto.SurveyQuestionDTO;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.model.Survey;
import com.monika.worek.orchestra.model.SurveyQuestion;
import com.monika.worek.orchestra.service.ProjectService;
import com.monika.worek.orchestra.service.SurveyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminSurveyController {

    private final SurveyService surveyService;
    private final ProjectService projectService;

    public AdminSurveyController(SurveyService surveyService, ProjectService projectService) {
        this.surveyService = surveyService;
        this.projectService = projectService;
    }

    @GetMapping("/admin/project/{projectId}/survey")
    public String showSurveyQuestions(@PathVariable Long projectId, Model model) {
        Project project = projectService.getProjectById(projectId);

        Survey survey = project.getSurvey() == null ? new Survey() : project.getSurvey();
        survey.setProject(project);
        surveyService.saveSurvey(survey);

        List<SurveyQuestion> questions = surveyService.findAllQuestionsBySurvey(survey);
        long missingSubmissions = surveyService.calculateMissingSubmissions(survey, project);

        model.addAttribute("projectId", project.getId());
        model.addAttribute("projectName", project.getName());
        model.addAttribute("survey", survey);
        model.addAttribute("questions", questions);
        model.addAttribute("surveyQuestionDTO", new SurveyQuestionDTO());
        model.addAttribute("missingSubmissions", missingSubmissions);
        return "/admin/admin-survey";
    }

    @PostMapping("/admin/project/{projectId}/survey")
    public String addSurveyQuestion(@PathVariable Long projectId,
                                    @Valid @ModelAttribute("surveyQuestionDTO") SurveyQuestionDTO dto,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        Project project = projectService.getProjectById(projectId);
        Survey survey = project.getSurvey();
        List<SurveyQuestion> questions = surveyService.findAllQuestionsBySurvey(survey);

        if (bindingResult.hasErrors()) {
            model.addAttribute("projectName", project.getName());
            model.addAttribute("survey", survey);
            model.addAttribute("questions", questions);
            return "/admin/admin-survey";
        }

        SurveyQuestion question = new SurveyQuestion();
        question.setQuestionText(dto.getQuestionText());
        question.setSurvey(survey);
        survey.getQuestions().add(question);

        surveyService.saveSurvey(survey);

        redirectAttributes.addFlashAttribute("success", "Survey question added.");
        return "redirect:/admin/project/" + projectId + "/survey";
    }

    @PostMapping("/admin/project/{projectId}/survey/{questionId}/delete")
    public String deleteSurveyQuestion(@PathVariable Long projectId, @PathVariable Long questionId, RedirectAttributes redirectAttributes) {
        surveyService.deleteQuestionById(questionId);
        redirectAttributes.addFlashAttribute("success", "Survey question deleted.");
        return "redirect:/admin/project/" + projectId + "/survey";
    }

    @PostMapping("/admin/project/{projectId}/survey/toggle")
    public String toggleSurveyStatus(@PathVariable Long projectId, RedirectAttributes redirectAttributes) {
        Project project = projectService.getProjectById(projectId);

        Survey survey = project.getSurvey();
        survey.setClosed(!survey.isClosed());
        projectService.saveProject(project);

        String status = survey.isClosed() ? "closed" : "reopened";
        redirectAttributes.addFlashAttribute("success", "Survey has been " + status + ".");

        return "redirect:/admin/project/" + projectId + "/survey";
    }
}
