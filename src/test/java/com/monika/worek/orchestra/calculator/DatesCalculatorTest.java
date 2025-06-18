package com.monika.worek.orchestra.calculator;

import com.monika.worek.orchestra.model.Project;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatesCalculatorTest {

    @Test
    void getAgreementDate_shouldReturnCorrectDate() {
        // given
        Project project = new Project();
        project.setStartDate(LocalDate.of(2025, 6, 20));

        // when
        LocalDate agreementDate = DatesCalculator.getAgreementDate(project);

        // then
        assertEquals(LocalDate.of(2025, 6, 10), agreementDate);
    }

    @Test
    void getResignationPenaltyDate_shouldReturnCorrectDate() {
        // given
        Project project = new Project();
        project.setStartDate(LocalDate.of(2025, 7, 15));

        // when
        LocalDate resignationPenaltyDate = DatesCalculator.getResignationPenaltyDate(project);

        // then
        assertEquals(LocalDate.of(2025, 7, 8), resignationPenaltyDate);
    }

    @Test
    void getPaymentDeadline_shouldReturnCorrectDate() {
        // given
        Project project = new Project();
        project.setEndDate(LocalDate.of(2025, 8, 1));

        // when
        LocalDate paymentDeadline = DatesCalculator.getPaymentDeadline(project);

        // then
        assertEquals(LocalDate.of(2025, 8, 15), paymentDeadline);
    }

    @Test
    void getInvoiceDate_shouldReturnCorrectDate() {
        // given
        Project project = new Project();
        project.setEndDate(LocalDate.of(2025, 9, 10));

        // when
        LocalDate invoiceDate = DatesCalculator.getInvoiceDate(project);

        // then
        assertEquals(LocalDate.of(2025, 9, 17), invoiceDate);
    }
}
