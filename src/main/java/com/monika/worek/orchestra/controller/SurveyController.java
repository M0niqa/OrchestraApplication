package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.SurveyQuestionDTO;
import com.monika.worek.orchestra.dto.SurveySubmissionDTO;
import com.monika.worek.orchestra.model.*;
import com.monika.worek.orchestra.repository.MusicianRepository;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.ProjectService;
import com.monika.worek.orchestra.service.SurveyService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class SurveyController {

    private final MusicianRepository musicianRepository;
    private final SurveyService surveyService;
    private final MusicianService musicianService;
    private final ProjectService projectService;

    public SurveyController(MusicianRepository musicianRepository, SurveyService surveyService, MusicianService musicianService, ProjectService projectService) {
        this.musicianRepository = musicianRepository;
        this.surveyService = surveyService;
        this.musicianService = musicianService;
        this.projectService = projectService;
    }

    @GetMapping("/musician/project/{projectId}/survey")
    public String showSurvey(@PathVariable Long projectId, Model model, Authentication authentication) {
        Musician musician = musicianRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Musician not found"));

        Optional<Survey> surveyOptional = surveyService.findByProjectId(projectId);

        if (surveyOptional.isEmpty() || surveyOptional.get().getQuestions().isEmpty()) {
            model.addAttribute("noSurvey", true);
            return "musician/musician-survey";
        }

        Survey survey = surveyOptional.get();

        if (survey.isClosed()) {
            model.addAttribute("surveyClosed", true);
            return "musician/musician-survey";
        }

        boolean surveySubmitted = surveyService.existsBySurveyAndMusician(survey, musician);
        model.addAttribute("surveySubmitted", surveySubmitted);
        if (surveySubmitted) {
            return "musician/musician-survey";
        }

        SurveySubmissionDTO submissionDTO = new SurveySubmissionDTO();
        model.addAttribute("submissionDTO", submissionDTO);
        model.addAttribute("projectId", survey.getProject().getId());
        model.addAttribute("projectName", survey.getProject().getName());
        model.addAttribute("questions", survey.getQuestions());

        return "musician/musician-survey";
    }

    @PostMapping("/musician/project/{projectId}/survey")
    public String submitSurvey(@PathVariable Long projectId, @ModelAttribute SurveySubmissionDTO submissionDTO, Authentication authentication, RedirectAttributes redirectAttributes) {
        Map<Long, String> responses = submissionDTO.getResponses();
        Musician musician = musicianService.getMusicianByEmail(authentication.getName());
        Survey survey = surveyService.findByProjectId(projectId).orElseThrow(() -> new IllegalArgumentException("Survey not found"));

        for (Map.Entry<Long, String> entry : responses.entrySet()) {
            Long questionId = entry.getKey();
            SurveyQuestion question = surveyService.findQuestionById(questionId);

            if (entry.getValue().equals("YES")) {
                question.setYesCount(question.getYesCount() + 1);
            } else {
                question.setNoCount(question.getNoCount() + 1);
            }
        }

        surveyService.saveQuestions(survey.getQuestions());
        surveyService.saveSubmission(new SurveySubmission(survey, musician));
        redirectAttributes.addFlashAttribute("submissionSuccess", "survey submitted successfully!");

        return "redirect:/musician/project/" + projectId + "/survey";
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
