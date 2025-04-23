package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.AgreementTemplate;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AgreementGenerationService {

    private static final Logger log = LoggerFactory.getLogger(AgreementGenerationService.class);

    public String generateAgreementContent(Project project, Musician musician) {
        Objects.requireNonNull(project, "Project cannot be null");
        Objects.requireNonNull(musician, "Musician cannot be null");
        log.info("Generating agreement content for Project ID: {} and Musician ID: {}", project.getId(), musician.getId());

        AgreementTemplate template = project.getAgreementTemplate();

        if (template == null) {
            log.error("Project ID {} ('{}') has no assigned agreement template.", project.getId(), project.getName());
            throw new IllegalStateException("Cannot generate agreement: Project '" + project.getName() + "' does not have an assigned template.");
        }
        log.debug("Using Template ID: {}", template.getId());

        String templateContent = template.getContent();
        if (templateContent == null || templateContent.isBlank()) {
            log.error("Template ID {} assigned to Project ID {} has no content.", template.getId(), project.getId());
            throw new IllegalStateException("Cannot generate agreement: The assigned template '" + "' is empty.");
        }

        Map<String, String> valuesMap = prepareValueMap(project, musician);
        log.debug("Prepared values map for substitution."); // Avoid logging the map itself if it contains sensitive data


        // 3. Perform substitution using Apache Commons Text StringSubstitutor
        //    Assumes placeholders in template are like ${key}
        StringSubstitutor sub = new StringSubstitutor(valuesMap, "${", "}"); // Define prefix/suffix for placeholders
        String generatedText = sub.replace(templateContent);
        log.info("Successfully generated agreement content for Project ID: {}", project.getId());

        return generatedText;
    }

    private Map<String, String> prepareValueMap(Project project, Musician musician) {
        Map<String, String> valuesMap = new HashMap<>();
        String na = "N/A";

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


        // --- Musician Data (with null checks) ---
        valuesMap.put("musician.fullName", String.format("%s %s",
                Objects.toString(musician.getFirstName(), ""),
                Objects.toString(musician.getLastName(), "")).trim());
        valuesMap.put("musician.firstName", Objects.toString(musician.getFirstName(), na));
        valuesMap.put("musician.lastName", Objects.toString(musician.getLastName(), na));
        valuesMap.put("musician.email", Objects.toString(musician.getEmail(), na));
        valuesMap.put("musician.pesel", maskData(musician.getPesel()));
        valuesMap.put("musician.address", musician.getAddress() != null ? musician.getAddress() : na);
        valuesMap.put("musician.bankAccountNumber", maskData(musician.getBankAccountNumber()));
        valuesMap.put("musician.taxOffice", musician.getTaxOffice() != null ? musician.getTaxOffice().getDisplayName() : na);

        valuesMap.put("project.name", Objects.toString(project.getName(), na));
        valuesMap.put("project.description", Objects.toString(project.getDescription(), na));
        valuesMap.put("project.startDate", project.getStartDate() != null ? project.getStartDate().format(dateFormatter) : na);
        valuesMap.put("project.endDate", project.getEndDate() != null ? project.getEndDate().format(dateFormatter) : na);
        //valuesMap.put("project.location", Objects.toString(project.getLocation(), na));
        //valuesMap.put("project.wage", project.getWage() != null ? project.getWage().toString() : na); // Uncommented

        valuesMap.put("current.date", LocalDate.now().format(dateFormatter));

        return valuesMap;
    }

    // Enhanced masking logic
    private String maskData(String data) {
        if (data == null || data.isBlank()) {
            return "******"; // Or N/A
        }
        // Example: Mask Polish IBAN (PL + 26 digits) - show PL and last 4 digits
        if (data.matches("PL\\d{26}")) {
            return "PL**************************" + data.substring(data.length() - 4);
        }
        // Example: Mask PESEL (11 digits) - heavily masked
        else if (data.matches("\\d{11}")) {
            return "*******" + data.substring(data.length()-3);
        }
        // Default generic masking for other potentially sensitive data
        else if (data.length() > 4) {
            return "****" + data.substring(data.length() - 4);
        } else {
            return "****"; // Mask short strings completely
        }
    }
}