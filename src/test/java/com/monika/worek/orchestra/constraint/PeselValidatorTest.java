package com.monika.worek.orchestra.constraint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PeselValidatorTest {

    private PeselValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PeselValidator();
    }

    @Test
    void isValid_whenPeselIsNull_thenShouldReturnTrue() {
        // given
        String pesel = null;

        // when
        boolean isValid = validator.isValid(pesel, null);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void isValid_whenPeselIsBlank_thenShouldReturnTrue() {
        // given
        String pesel = "   ";

        // when
        boolean isValid = validator.isValid(pesel, null);

        // then
        assertThat(isValid).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678903",
            "53020155371",
            "84011201654"
    })
    void isValid_whenPeselIsValid_thenShouldReturnTrue(String validPesel) {
        // given: provided by the @ValueSource

        // when
        boolean isValid = validator.isValid(validPesel, null);

        // then
        assertThat(isValid).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567890",       // too short
            "123456789012",     // too long
            "1234567890A",      // contains a letter
            "90010112346"       // correct length and format, but invalid checksum
    })
    void isValid_whenPeselIsInvalid_thenShouldReturnFalse(String invalidPesel) {
        // given: provided by the @ValueSource

        // when
        boolean isValid = validator.isValid(invalidPesel, null);

        // then
        assertThat(isValid).isFalse();
    }
}
