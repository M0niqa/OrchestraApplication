package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import com.monika.worek.orchestra.repository.AgreementTemplateRepository;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AgreementService {

    private final AgreementTemplateRepository templateRepository;

    public AgreementService(AgreementTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public String generateAgreementContent(Project project, Musician musician) {
        Objects.requireNonNull(project, "Project cannot be null");
        Objects.requireNonNull(musician, "Musician cannot be null");

        String templateContent = getTemplateContent(project);
        Map<String, String> valuesMap = prepareValueMap(project, musician);
        StringSubstitutor substitutor = new StringSubstitutor(valuesMap, "${", "}");

        return substitutor.replace(templateContent);
    }

    private static String getTemplateContent(Project project) {
        AgreementTemplate template = project.getAgreementTemplate();

        if (template == null) {
            throw new IllegalStateException("Cannot generate agreement: Project '" + project.getName() + "' does not have an assigned template.");
        }

        String templateContent = template.getContent();
        if (templateContent == null || templateContent.isBlank()) {
            throw new IllegalStateException("Cannot generate agreement: The assigned template '" + "' is empty.");
        }
        return templateContent;
    }

    private Map<String, String> prepareValueMap(Project project, Musician musician) {
        Map<String, String> valuesMap = new HashMap<>();
        String na = "N/A";

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        valuesMap.put("current.date", LocalDate.now().format(dateFormatter));
        valuesMap.put("musician.fullName", String.format("%s %s",
                Objects.toString(musician.getFirstName(), ""),
                Objects.toString(musician.getLastName(), "")).trim());
        valuesMap.put("musician.address", musician.getAddress() != null ? musician.getAddress() : na);
        valuesMap.put("musician.bankAccountNumber", musician.getBankAccountNumber());
        valuesMap.put("musician.instrument", musician.getInstrument().toString());

        valuesMap.put("project.endDate", project.getEndDate() != null ? project.getEndDate().format(dateFormatter) : na);
        valuesMap.put("project.location", Objects.toString(project.getLocation(), na));
        valuesMap.put("project.conductor", project.getConductor() != null ? project.getConductor() : na);
        valuesMap.put("project.programme", Objects.toString(project.getProgramme(), na));
        valuesMap.put("paymentDeadline", Objects.toString(getPaymentDeadline(project), na));
        valuesMap.put("wage", Objects.toString(getWage(project, musician), na));
        valuesMap.put("wageNet", Objects.toString(getNetWage(project, musician), na));

        return valuesMap;
    }

    public void saveTemplate(AgreementTemplate agreementTemplate) {
        templateRepository.save(agreementTemplate);
    }

    private BigDecimal getWage(Project project, Musician musician) {
        String group = musician.getInstrument() != null ? musician.getInstrument().getGroup() : null;
        if (group != null) {
            return project.getGroupSalaries().get(group);
        }
        return null;
    }

    private BigDecimal getNetWage(Project project, Musician musician) {
        BigDecimal grossWage = getWage(project, musician);
        if (grossWage == null) {
            return BigDecimal.ZERO;
        }
        return grossWage.multiply(BigDecimal.valueOf(0.91)).setScale(2, RoundingMode.HALF_UP);
    }

    private LocalDate getPaymentDeadline(Project project) {
        return project.getEndDate().plusDays(14);
    }
}