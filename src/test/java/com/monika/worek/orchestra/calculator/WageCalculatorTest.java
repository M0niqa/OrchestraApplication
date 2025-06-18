package com.monika.worek.orchestra.calculator;

import com.monika.worek.orchestra.model.Instrument;
import com.monika.worek.orchestra.model.Musician;
import com.monika.worek.orchestra.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WageCalculatorTest {

    @Mock
    private Project mockProject;

    @Mock
    private Musician mockMusician;

    @Test
    void getGrossWage_ifGroupExists_thenShouldReturnCorrectWage() {
        // given
        Instrument violinI = Instrument.VIOLIN_I;
        String group = violinI.getGroup(); // "Strings"
        BigDecimal expectedWage = new BigDecimal("1000.00");
        Map<String, BigDecimal> groupSalaries = new HashMap<>();
        groupSalaries.put(group, expectedWage);

        when(mockMusician.getInstrument()).thenReturn(violinI);
        when(mockProject.getGroupSalaries()).thenReturn(groupSalaries);

        // when
        BigDecimal actualWage = WageCalculator.getGrossWage(mockProject, mockMusician);

        // then
        Assertions.assertNotNull(actualWage);
        assertEquals(expectedWage.setScale(2, RoundingMode.HALF_UP), actualWage.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void getGrossWage_ifGroupDoesNotExist_thenShouldReturnNull() {
        // given
        Instrument clarinet = Instrument.CLARINET; // "Winds"
        Map<String, BigDecimal> groupSalaries = new HashMap<>();
        groupSalaries.put("Strings", new BigDecimal("1000.00"));

        when(mockMusician.getInstrument()).thenReturn(clarinet);
        when(mockProject.getGroupSalaries()).thenReturn(groupSalaries);

        // when
        BigDecimal actualWage = WageCalculator.getGrossWage(mockProject, mockMusician);

        // then
        assertNull(actualWage);
    }

    @Test
    void getGrossWage_ifMusicianHasNoInstrument_thenShouldReturnNull() {
        // given
        when(mockMusician.getInstrument()).thenReturn(null);

        // when
        BigDecimal actualWage = WageCalculator.getGrossWage(mockProject, mockMusician);

        // then
        assertNull(actualWage);
    }

    @Test
    void getNetWage_ifGrossWageExists_thenShouldCalculateCorrectly() {
        // given
        BigDecimal grossWage = new BigDecimal("1000.00");
        BigDecimal expectedNetWage = new BigDecimal("910.00"); // 1000 * 0.91

        when(mockMusician.getInstrument()).thenReturn(Instrument.VIOLIN_I);
        Map<String, BigDecimal> groupSalaries = new HashMap<>();
        groupSalaries.put("Strings", grossWage);
        when(mockProject.getGroupSalaries()).thenReturn(groupSalaries);

        // when
        BigDecimal actualNetWage = WageCalculator.getNetWage(mockProject, mockMusician);

        // then
        Assertions.assertNotNull(actualNetWage);
        assertEquals(expectedNetWage.setScale(2, RoundingMode.HALF_UP), actualNetWage.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void getNetWage_ifGrossWageIsNull_thenShouldReturnNull() {
        // given
        when(mockMusician.getInstrument()).thenReturn(null);

        // when
        BigDecimal actualNetWage = WageCalculator.getNetWage(mockProject, mockMusician);

        // then
        assertNull(actualNetWage);
    }

    @Test
    void getCostOfIncome_ifGrossWageExists_thenShouldCalculateCorrectly() {
        // given
        BigDecimal grossWage = new BigDecimal("1000.00");
        BigDecimal expectedCost = new BigDecimal("500.00"); // 1000 * 0.5

        when(mockMusician.getInstrument()).thenReturn(Instrument.VIOLIN_I);
        Map<String, BigDecimal> groupSalaries = new HashMap<>();
        groupSalaries.put("Strings", grossWage);
        when(mockProject.getGroupSalaries()).thenReturn(groupSalaries);

        // when
        BigDecimal actualCost = WageCalculator.getCostOfIncome(mockProject, mockMusician);

        // then
        Assertions.assertNotNull(actualCost);
        assertEquals(expectedCost.setScale(2, RoundingMode.HALF_UP), actualCost.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void getCostOfIncome_ifGrossWageIsNull_thenShouldReturnNull() {
        // given
        when(mockMusician.getInstrument()).thenReturn(null);

        // when
        BigDecimal actualCost = WageCalculator.getCostOfIncome(mockProject, mockMusician);

        // then
        assertNull(actualCost);
    }

    @Test
    void getTax_ifGrossWageExists_thenShouldCalculateCorrectly() {
        // given
        BigDecimal grossWage = new BigDecimal("1000.00");
        BigDecimal expectedTax = new BigDecimal("90.00"); // 1000 * 0.09

        when(mockMusician.getInstrument()).thenReturn(Instrument.VIOLIN_I);
        Map<String, BigDecimal> groupSalaries = new HashMap<>();
        groupSalaries.put("Strings", grossWage);
        when(mockProject.getGroupSalaries()).thenReturn(groupSalaries);

        // when
        BigDecimal actualTax = WageCalculator.getTax(mockProject, mockMusician);

        // then
        Assertions.assertNotNull(actualTax);
        assertEquals(expectedTax.setScale(2, RoundingMode.HALF_UP), actualTax.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void getTax_ifGrossWageIsNull_thenShouldReturnNull() {
        // given
        when(mockMusician.getInstrument()).thenReturn(null);

        // when
        BigDecimal actualTax = WageCalculator.getTax(mockProject, mockMusician);

        // then
        assertNull(actualTax);
    }
}

