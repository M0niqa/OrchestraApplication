package com.monika.worek.orchestra.constraint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class NipValidatorTest {

    private NipValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NipValidator();
    }

    @Test
    void isValid_whenNipIsNull_thenShouldReturnTrue() {
        // given
        String nip = null;

        // when
        boolean isValid = validator.isValid(nip, null);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void isValid_whenNipIsBlank_thenShouldReturnTrue() {
        // given
        String nip = "   ";

        // when
        boolean isValid = validator.isValid(nip, null);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void isValid_whenNipIsValid10DigitNumber_thenShouldReturnTrue() {
        // given
        String nip = "1234567890";

        // when
        boolean isValid = validator.isValid(nip, null);

        // then
        assertThat(isValid).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345",          // too short
            "12345678901",    // too long
            "123456789A",     // contains a letter
            "123-456-78-90"   // contains dashes
    })
    void isValid_whenNipIsInvalid_thenShouldReturnFalse(String invalidNip) {
        // given: provided by the @ValueSource

        // when
        boolean isValid = validator.isValid(invalidNip, null);

        // then
        assertThat(isValid).isFalse();
    }
}