package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.SurveyQuestionDTO;
import com.monika.worek.orchestra.dto.SurveySubmissionDTO;
import com.monika.worek.orchestra.model.*;
import com.monika.worek.orchestra.repository.MusicianRepository;
import com.monika.worek.orchestra.repository.ProjectRepository;
import com.monika.worek.orchestra.repository.SurveyQuestionRepository;
import com.monika.worek.orchestra.repository.SurveyRepository;
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
import java.util.stream.Collectors;

@Controller
public class SurveyController {

    private final ProjectRepository projectRepository;
    private final MusicianRepository musicianRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;

    public SurveyController(ProjectRepository projectRepository, MusicianRepository musicianRepository, SurveyRepository surveyRepository, SurveyQuestionRepository surveyQuestionRepository) {
        this.projectRepository = projectRepository;
        this.musicianRepository = musicianRepository;
        this.surveyRepository = surveyRepository;
        this.surveyQuestionRepository = surveyQuestionRepository;
    }

    @GetMapping("/musician/project/{projectId}/survey")
    public String showSurvey(@PathVariable Long projectId, Model model, Authentication authentication) {
        Musician musician = musicianRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Musician not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        List<SurveyQuestion> questions = surveyQuestionRepository.findByProject(project);

        if (questions.isEmpty()) {
            model.addAttribute("noSurvey", true);  // Flag for no questions
        } else {
            Survey existingSurvey = surveyRepository.findByMusicianAndProject(musician, project).orElse(null);
            boolean surveySubmitted = existingSurvey != null;

            model.addAttribute("surveySubmitted", surveySubmitted);
            if (surveySubmitted) {
                Map<Long, SurveyAnswer> responsesMap = existingSurvey.getResponses().stream()
                        .collect(Collectors.toMap(
                                r -> r.getQuestion().getId(),
                                SurveyResponse::getAnswer
                        ));
                model.addAttribute("responsesMap", responsesMap);
            } else {
                SurveySubmissionDTO submissionDTO = new SurveySubmissionDTO();
                questions.forEach(q -> submissionDTO.getResponses().put(q.getId(), null));
                model.addAttribute("submissionDTO", submissionDTO);
            }
        }

        model.addAttribute("project", project);
        model.addAttribute("questions", questions);
        return "survey";
    }

    @PostMapping("/musician/project/{projectId}/survey")
    public String submitSurvey(@PathVariable Long projectId,
                               @ModelAttribute SurveySubmissionDTO submissionDTO,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {

        Musician musician = musicianRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Musician not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (surveyRepository.findByMusicianAndProject(musician, project).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Survey already submitted.");
            return "redirect:/musician/project/" + projectId + "/survey";
        }

        Survey survey = new Survey();
        survey.setMusician(musician);
        survey.setProject(project);
        List<SurveyResponse> responses = submissionDTO.getResponses().entrySet().stream()
                .map(entry -> {
                    SurveyResponse response = new SurveyResponse();
                    response.setQuestion(surveyQuestionRepository.findById(entry.getKey()).orElseThrow());
                    response.setAnswer(SurveyAnswer.valueOf(entry.getValue()));
                    response.setSurvey(survey);
                    return response;
                }).collect(Collectors.toList());

        survey.setResponses(responses);
        surveyRepository.save(survey);

        redirectAttributes.addFlashAttribute("submissionSuccess", "Survey submitted!");
        return "redirect:/musician/project/" + projectId + "/survey";
    }

    @GetMapping("/admin/project/{projectId}/survey")
    public String showSurveyQuestions(@PathVariable Long projectId, Model model) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        List<SurveyQuestion> questions = surveyQuestionRepository.findByProject(project);

        model.addAttribute("project", project);
        model.addAttribute("questions", questions);
        model.addAttribute("surveyQuestionDTO", new SurveyQuestionDTO());
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

        if (bindingResult.hasErrors()) {
            List<SurveyQuestion> questions = surveyQuestionRepository.findByProject(project);
            model.addAttribute("project", project);
            model.addAttribute("questions", questions);
            return "admin-survey-questions";
        }

        SurveyQuestion question = new SurveyQuestion();
        question.setProject(project);
        question.setQuestionText(dto.getQuestionText());

        System.out.println("Saving question: " + dto.getQuestionText());
        surveyQuestionRepository.save(question);

        redirectAttributes.addFlashAttribute("success", "Survey question added.");
        return "redirect:/admin/project/" + projectId + "/survey";
    }

}
