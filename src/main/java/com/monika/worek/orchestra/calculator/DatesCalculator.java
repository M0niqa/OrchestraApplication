package com.monika.worek.orchestra.calculator;

import com.monika.worek.orchestra.model.Project;

import java.time.LocalDate;

public class DatesCalculator {

    public static LocalDate getAgreementDate(Project project) {
        return project.getStartDate().minusDays(10);
    }

    public static LocalDate getResignationPenaltyDate(Project project) {
        return project.getStartDate().minusDays(7);
    }

    public static LocalDate getPaymentDeadline(Project project) {
        return project.getEndDate().plusDays(14);
    }

    public static LocalDate getInvoiceDate(Project project) {
        return project.getEndDate().plusDays(7);
    }
}
