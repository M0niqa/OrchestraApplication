package com.monika.worek.orchestra.calculator;

import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class WageCalculator {

    public static BigDecimal getGrossWage(Project project, Musician musician) {
        if (project == null || project.getGroupSalaries() == null || musician == null || musician.getInstrument() == null) {
            return null;
        }
        String group = musician.getInstrument().getGroup();
        return project.getGroupSalaries().get(group);
    }

    public static BigDecimal getNetWage(Project project, Musician musician) {
        if (project == null || musician == null || musician.getInstrument() == null) {
            return null;
        }
        BigDecimal grossWage = getGrossWage(project, musician);
        if (grossWage != null) {
            return grossWage.multiply(BigDecimal.valueOf(0.91)).setScale(2, RoundingMode.HALF_UP);
        }
        return null;
    }

    public static BigDecimal getCostOfIncome(Project project, Musician musician) {
        if (project == null || musician == null || musician.getInstrument() == null) {
            return null;
        }
        BigDecimal grossWage = getGrossWage(project, musician);
        if (grossWage != null) {
            return grossWage.multiply(BigDecimal.valueOf(0.5)).setScale(2, RoundingMode.HALF_UP);
        }
        return null;
    }

    public static BigDecimal getTax(Project project, Musician musician) {
        BigDecimal grossWage = getGrossWage(project, musician);
        if (grossWage == null) {
            return null;
        }
        return grossWage.multiply(BigDecimal.valueOf(0.09)).setScale(2, RoundingMode.HALF_UP);
    }
}
