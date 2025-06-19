package com.monika.worek.orchestra.constraint;

import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class StartDateBeforeEndDateValidatorTest {

    private StartDateBeforeEndDateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StartDateBeforeEndDateValidator();
    }

    @Test
    void isValid_whenStartDateIsAfterEndDate_thenShouldReturnFalse() {
        // given
        ProjectBasicInfoDTO dto = ProjectBasicInfoDTO.builder()
                .startDate(LocalDate.of(2025, 8, 20))
                .endDate(LocalDate.of(2025, 8, 10))
                .build();

        // when
        boolean isValid = validator.isValid(dto, null);

        // then
        assertFalse(isValid);
    }

    @Test
    void isValid_whenStartDateIsNull_thenShouldReturnTrue() {
        // given
        ProjectBasicInfoDTO dto = ProjectBasicInfoDTO.builder()
                .startDate(null)
                .endDate(LocalDate.of(2025, 8, 10))
                .build();

        // when
        boolean isValid = validator.isValid(dto, null);

        // then
        assertTrue(isValid);
    }

    @Test
    void isValid_whenEndDateIsNull_thenShouldReturnTrue() {
        // given
        ProjectBasicInfoDTO dto = ProjectBasicInfoDTO.builder()
                .startDate(LocalDate.of(2025, 8, 10))
                .endDate(null)
                .build();

        // when
        boolean isValid = validator.isValid(dto, null);

        // then
        assertTrue(isValid);
    }

    @Test
    void isValid_whenStartDateIsSameAsEndDate_thenShouldReturnTrue() {
        // given
        ProjectBasicInfoDTO dto = ProjectBasicInfoDTO.builder()
                .startDate(LocalDate.of(2025, 8, 10))
                .endDate(LocalDate.of(2025, 8, 10))
                .build();

        // when
        boolean isValid = validator.isValid(dto, null);

        // then
        assertTrue(isValid);
    }

    @Test
    void isValid_whenStartDateIsBeforeEndDate_thenShouldReturnTrue() {
        // given
        ProjectBasicInfoDTO dto = ProjectBasicInfoDTO.builder()
                .startDate(LocalDate.of(2025, 8, 10))
                .endDate(LocalDate.of(2025, 8, 20))
                .build();

        // when
        boolean isValid = validator.isValid(dto, null);

        // then
        assertTrue(isValid);
    }
}