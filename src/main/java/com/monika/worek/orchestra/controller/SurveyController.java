package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.SurveyQuestionDTO;
import com.monika.worek.orchestra.dto.SurveySubmissionDTO;
import com.monika.worek.orchestra.model.*;
import com.monika.worek.orchestra.repository.MusicianRepository;
import com.monika.worek.orchestra.repository.ProjectRepository;
import com.monika.worek.orchestra.repository.SurveyQuestionRepository;
import com.monika.worek.orchestra.repository.SurveyRepository;
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

@Controller
public class SurveyController {

    private final ProjectRepository projectRepository;
    private final MusicianRepository musicianRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyService surveyService;

    public SurveyController(ProjectRepository projectRepository, MusicianRepository musicianRepository, SurveyRepository surveyRepository, SurveyQuestionRepository surveyQuestionRepository, SurveyService surveyService) {
        this.projectRepository = projectRepository;
        this.musicianRepository = musicianRepository;
        this.surveyRepository = surveyRepository;
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.surveyService = surveyService;
    }

    @GetMapping("/musician/project/{projectId}/survey")
    public String showSurvey(@PathVariable Long projectId, Model model, Authentication authentication) {
        Musician musician = musicianRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Musician not found"));

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        Survey survey = project.getSurvey();
        model.addAttribute("project", project);

        if (survey == null || survey.getQuestions().isEmpty()) {
            model.addAttribute("noSurvey", true);
            return "survey";
        }

        if (survey.isClosed()) {
            model.addAttribute("surveyClosed", true);
            return "survey";
        }

        boolean surveySubmitted = surveyService.existsBySurveyAndMusician(survey, musician);
        model.addAttribute("surveySubmitted", surveySubmitted);
        if (surveySubmitted) {
            return "survey";
        }

        SurveySubmissionDTO submissionDTO = new SurveySubmissionDTO();
        model.addAttribute("submissionDTO", submissionDTO);
        model.addAttribute("project", survey.getProject());
        model.addAttribute("questions", survey.getQuestions());

        return "survey";
    }

    @PostMapping("/musician/project/{projectId}/survey")
    public String submitSurvey(@PathVariable Long projectId, @ModelAttribute SurveySubmissionDTO submissionDTO,
                               Authentication authentication) {

        Map<Long, String> responses = submissionDTO.getResponses();
        Musician musician = musicianRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Musician not found"));
        Survey survey = surveyRepository.findByProjectId(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));

        for (Map.Entry<Long, String> entry : responses.entrySet()) {
            Long questionId = entry.getKey();
            SurveyQuestion question = surveyQuestionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalArgumentException("Question not found"));

            if (entry.getValue().equals("YES")) {
                question.setYesCount(question.getYesCount() + 1);
            } else {
                question.setNoCount(question.getNoCount() + 1);
            }
        }

        surveyQuestionRepository.saveAll(survey.getQuestions());
        surveyService.saveSubmission(new SurveySubmission(survey, musician));

        return "redirect:/musician/project/" + projectId + "/survey";
    }

    @GetMapping("/admin/project/{projectId}/survey")
    public String showSurveyQuestions(@PathVariable Long projectId, Model model) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        Survey survey = project.getSurvey() == null ? new Survey() : project.getSurvey();
        survey.setProject(project);
        surveyRepository.save(survey);

        List<SurveyQuestion> questions = surveyQuestionRepository.findAllBySurvey(survey);
        long missingSubmissions = surveyService.calculateMissingSubmissions(survey, project);

        model.addAttribute("project", project);
        model.addAttribute("survey", survey);
        model.addAttribute("questions", questions);
        model.addAttribute("surveyQuestionDTO", new SurveyQuestionDTO());
        model.addAttribute("missingSubmissions", missingSubmissions);
        return "admin-survey-questions";
    }

    @PostMapping("/admin/project/{projectId}/survey")
    public String addSurveyQuestion(@PathVariable Long projectId,
                                    @Valid @ModelAttribute("surveyQuestionDTO") SurveyQuestionDTO dto,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        Survey survey = project.getSurvey();
        List<SurveyQuestion> questions = surveyQuestionRepository.findAllBySurvey(survey);

        if (bindingResult.hasErrors()) {
            model.addAttribute("project", project);
            model.addAttribute("survey", survey);
            model.addAttribute("questions", questions);
            return "admin-survey-questions";
        }

        SurveyQuestion question = new SurveyQuestion();
        question.setQuestionText(dto.getQuestionText());
        question.setSurvey(survey);
        survey.getQuestions().add(question);

        surveyRepository.save(survey);

        redirectAttributes.addFlashAttribute("success", "Survey question added.");
        return "redirect:/admin/project/" + projectId + "/survey";
    }

    @PostMapping("/admin/project/{projectId}/survey/{questionId}/delete")
    public String deleteSurveyQuestion(@PathVariable Long projectId,
                                       @PathVariable Long questionId,
                                       RedirectAttributes redirectAttributes) {
        surveyQuestionRepository.deleteById(questionId);
        redirectAttributes.addFlashAttribute("success", "Survey question deleted.");
        return "redirect:/admin/project/" + projectId + "/survey";
    }

    @PostMapping("/admin/project/{projectId}/survey/toggle")
    public String toggleSurveyStatus(@PathVariable Long projectId, RedirectAttributes redirectAttributes) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        Survey survey = project.getSurvey();
        survey.setClosed(!survey.isClosed());
        projectRepository.save(project);

        String status = survey.isClosed() ? "closed" : "reopened";
        redirectAttributes.addFlashAttribute("success", "Survey has been " + status + ".");

        return "redirect:/admin/project/" + projectId + "/survey";
    }

}
