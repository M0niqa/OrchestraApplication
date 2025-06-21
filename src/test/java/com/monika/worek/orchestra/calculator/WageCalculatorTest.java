package com.monika.worek.orchestra.calculator;

import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;


public class WageCalculatorTest {

    @Test
    void getGrossWage_whenProjectIsNull_thenShouldReturnNull() {
        // given
        Project project = null;
        Musician musician = Musician.builder().build();

        // when
        BigDecimal result = WageCalculator.getGrossWage(project, musician);

        // then
        assertNull(result);
    }

    @Test
    void getGrossWage_whenMusicianIsNull_thenShouldReturnNull() {
        // given
        Project project = Project.builder().build();
        Musician musician = null;

        // when
        BigDecimal result = WageCalculator.getGrossWage(project, musician);

        // then
        assertNull(result);
    }

    @Test
    void getGrossWage_whenGroupSalariesMapIsNull_thenShouldReturnNull() {
        // given
        Project project = Project.builder().groupSalaries(null).build();
        Musician musician = Musician.builder().instrument(Instrument.VIOLIN_I).build();

        // when
        BigDecimal result = WageCalculator.getGrossWage(project, musician);

        // then
        assertNull(result);
    }

    @Test
    void getGrossWage_whenMusicianInstrumentIsNull_thenShouldReturnNull() {
        // given
        Project project = Project.builder().groupSalaries(new HashMap<>()).build();
        Musician musician = Musician.builder().instrument(null).build();

        // when
        BigDecimal result = WageCalculator.getGrossWage(project, musician);

        // then
        assertNull(result);
    }

    @Test
    void getGrossWage_whenAllInputsAreValid_thenShouldReturnCorrectWageFromMap() {
        // given
        Musician musician = Musician.builder().instrument(Instrument.VIOLIN_I).build(); // Belongs to "Strings" group

        Map<String, BigDecimal> groupSalaries = new HashMap<>();
        groupSalaries.put("Strings", new BigDecimal("1000"));
        groupSalaries.put("Winds", new BigDecimal("1100"));

        Project project = Project.builder()
                .groupSalaries(groupSalaries)
                .build();

        // when
        BigDecimal result = WageCalculator.getGrossWage(project, musician);

        // then
        assertEquals(BigDecimal.valueOf(1000), result);
    }

    @Test
    void getNetWage_whenProjectIsNull_thenShouldReturnNull() {
        // given
        Project project = null;
        Musician musician = Musician.builder().build();

        // when
        BigDecimal result = WageCalculator.getNetWage(project, musician);

        // then
        assertNull(result);
    }

    @Test
    void getNetWage_whenMusicianIsNull_thenShouldReturnNull() {
        // given
        Project project = Project.builder().build();
        Musician musician = null;

        // when
        BigDecimal result = WageCalculator.getNetWage(project, musician);

        // then
        assertNull(result);
    }

    @Test
    void getNetWage_whenGrossWageIsNull_thenShouldReturnNull() {
        // given
        Project project = Project.builder().build();
        Musician musician = Musician.builder().instrument(Instrument.VIOLIN_I).build();

        try (MockedStatic<WageCalculator> mockedCalculator = mockStatic(WageCalculator.class)) {
            mockedCalculator.when(() -> WageCalculator.getNetWage(project, musician)).thenCallRealMethod();
            mockedCalculator.when(() -> WageCalculator.getGrossWage(project, musician)).thenReturn(null);

            // when
            BigDecimal result = WageCalculator.getNetWage(project, musician);

            // then
            assertNull(result);
        }
    }

    @Test
    void getNetWage_whenGrossWageIsValid_thenShouldReturnCorrectlyCalculatedNetWage() {
        // given
        Project project = Project.builder().build();
        Musician musician = Musician.builder().instrument(Instrument.VIOLIN_I).build();
        BigDecimal grossWage = new BigDecimal("1000.00");

        // Use try-with-resources to mock the static getGrossWage method
        try (MockedStatic<WageCalculator> mockedCalculator = mockStatic(WageCalculator.class)) {
            mockedCalculator.when(() -> WageCalculator.getNetWage(project, musician)).thenCallRealMethod();
            mockedCalculator.when(() -> WageCalculator.getGrossWage(project, musician)).thenReturn(grossWage);

            // when
            BigDecimal result = WageCalculator.getNetWage(project, musician);

            // then
            assertEquals(new BigDecimal("910.00"), result);
        }
    }

    @Test
    void getNetWage_whenCalculationNeedsRounding_thenShouldRoundCorrectly() {
        // given
        Project project = Project.builder().build();
        Musician musician = Musician.builder().instrument(Instrument.VIOLIN_I).build();
        BigDecimal grossWage = new BigDecimal("1000");

        try (MockedStatic<WageCalculator> mockedCalculator = mockStatic(WageCalculator.class)) {
            mockedCalculator.when(() -> WageCalculator.getNetWage(project, musician)).thenCallRealMethod();
            mockedCalculator.when(() -> WageCalculator.getGrossWage(project, musician)).thenReturn(grossWage);

            // when
            BigDecimal result = WageCalculator.getNetWage(project, musician);

            // then
            assertEquals(new BigDecimal("910.00"), result);
        }
    }

    @Test
    void getCostOfIncome_whenProjectIsNull_thenShouldReturnNull() {
        // given
        Project project = null;
        Musician musician = Musician.builder().build();

        // when
        BigDecimal result = WageCalculator.getCostOfIncome(project, musician);

        // then
        assertNull(result);
    }

    @Test
    void getCostOfIncome_whenMusicianIsNull_thenShouldReturnNull() {
        // given
        Project project = Project.builder().build();
        Musician musician = null;

        // when
        BigDecimal result = WageCalculator.getCostOfIncome(project, musician);

        // then
        assertNull(result);
    }

    @Test
    void getCostOfIncome_whenGrossWageIsNull_thenShouldReturnNull() {
        // given
        Project project = Project.builder().build();
        Musician musician = Musician.builder().instrument(Instrument.VIOLIN_I).build();

        try (MockedStatic<WageCalculator> mockedCalculator = mockStatic(WageCalculator.class)) {
            mockedCalculator.when(() -> WageCalculator.getCostOfIncome(project, musician)).thenCallRealMethod();
            mockedCalculator.when(() -> WageCalculator.getGrossWage(project, musician)).thenReturn(null);

            // when
            BigDecimal result = WageCalculator.getCostOfIncome(project, musician);

            // then
            assertNull(result);
        }
    }

    @Test
    void getCostOfIncome_whenGrossWageIsValid_thenShouldReturnCorrectlyCalculatedValue() {
        // given
        Project project = Project.builder().build();
        Musician musician = Musician.builder().instrument(Instrument.VIOLIN_I).build();
        BigDecimal grossWage = new BigDecimal("1200.50");

        try (MockedStatic<WageCalculator> mockedCalculator = mockStatic(WageCalculator.class)) {
            mockedCalculator.when(() -> WageCalculator.getCostOfIncome(project, musician)).thenCallRealMethod();
            mockedCalculator.when(() -> WageCalculator.getGrossWage(project, musician)).thenReturn(grossWage);

            // when
            BigDecimal result = WageCalculator.getCostOfIncome(project, musician);

            // then
            assertEquals(new BigDecimal("600.25"), result);
        }
    }

    @Test
    void getCostOfIncome_whenCalculationNeedsRounding_thenShouldRoundCorrectly() {
        // given
        Project project = Project.builder().build();
        Musician musician = Musician.builder().instrument(Instrument.VIOLIN_I).build();
        BigDecimal grossWage = new BigDecimal("1101.51");

        try (MockedStatic<WageCalculator> mockedCalculator = mockStatic(WageCalculator.class)) {
            mockedCalculator.when(() -> WageCalculator.getCostOfIncome(project, musician)).thenCallRealMethod();
            mockedCalculator.when(() -> WageCalculator.getGrossWage(project, musician)).thenReturn(grossWage);

            // when
            BigDecimal result = WageCalculator.getCostOfIncome(project, musician);

            // then
            assertEquals(new BigDecimal("550.76"), result);
        }
    }
    @Test
    void getTax_whenGrossWageIsNull_thenShouldReturnNull() {
        // given
        Project project = Project.builder().build();
        Musician musician = Musician.builder().build();

        try (MockedStatic<WageCalculator> mockedCalculator = mockStatic(WageCalculator.class)) {
            mockedCalculator.when(() -> WageCalculator.getTax(project, musician)).thenCallRealMethod();
            mockedCalculator.when(() -> WageCalculator.getGrossWage(project, musician)).thenReturn(null);

            // when
            BigDecimal result = WageCalculator.getTax(project, musician);

            // then
            assertNull(result);
        }
    }

    @Test
    void getTax_whenGrossWageIsValid_thenShouldReturnCorrectlyCalculatedTax() {
        // given
        Project project = Project.builder().build();
        Musician musician = Musician.builder().build();
        BigDecimal grossWage = new BigDecimal("1000.00");

        try (MockedStatic<WageCalculator> mockedCalculator = mockStatic(WageCalculator.class)) {
            mockedCalculator.when(() -> WageCalculator.getTax(project, musician)).thenCallRealMethod();
            mockedCalculator.when(() -> WageCalculator.getGrossWage(project, musician)).thenReturn(grossWage);

            // when
            BigDecimal result = WageCalculator.getTax(project, musician);

            // then
            assertEquals(new BigDecimal("90.00"), result);
        }
    }

    @Test
    void getTax_whenCalculationNeedsRounding_thenShouldRoundCorrectly() {
        // given
        Project project = Project.builder().build();
        Musician musician = Musician.builder().build();
        BigDecimal grossWage = new BigDecimal("1123.45");

        try (MockedStatic<WageCalculator> mockedCalculator = mockStatic(WageCalculator.class)) {
            mockedCalculator.when(() -> WageCalculator.getTax(project, musician)).thenCallRealMethod();
            mockedCalculator.when(() -> WageCalculator.getGrossWage(project, musician)).thenReturn(grossWage);

            // when
            BigDecimal result = WageCalculator.getTax(project, musician);

            // then
            assertEquals(new BigDecimal("101.11"), result);
        }
    }
}
