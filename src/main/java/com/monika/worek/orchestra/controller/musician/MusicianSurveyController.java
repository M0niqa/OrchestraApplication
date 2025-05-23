package com.monika.worek.orchestra.controller.musician;

import com.monika.worek.orchestra.dto.SurveySubmissionDTO;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Survey;
import com.monika.worek.orchestra.model.SurveyQuestion;
import com.monika.worek.orchestra.model.SurveySubmission;
import com.monika.worek.orchestra.service.MusicianService;
import com.monika.worek.orchestra.service.SurveyService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;


@Controller
public class MusicianSurveyController {

    private final MusicianService musicianService;
    private final SurveyService surveyService;

    public MusicianSurveyController(MusicianService musicianService, SurveyService surveyService) {
        this.musicianService = musicianService;
        this.surveyService = surveyService;
    }

    @GetMapping("/musician/project/{projectId}/survey")
    public String showSurvey(@PathVariable Long projectId, Model model, Authentication authentication) {
        Musician musician = musicianService.getMusicianByEmail(authentication.getName());

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
}
