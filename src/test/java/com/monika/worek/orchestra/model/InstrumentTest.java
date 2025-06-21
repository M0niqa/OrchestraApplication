package com.monika.worek.orchestra.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class InstrumentTest {

    @ParameterizedTest(name = "getGroup_whenInstrumentIs{0}_thenShouldReturn{1}")
    @CsvSource({
            "VIOLIN_I,     Strings",
            "VIOLIN_II,    Strings",
            "VIOLA,        Strings",
            "CELLO,        Strings",
            "DOUBLE_BASS,  Strings",
            "FLUTE,        Winds",
            "OBOE,         Winds",
            "CLARINET,     Winds",
            "BASSOON,      Winds",
            "TRUMPET,      Brass",
            "FRENCH_HORN,  Brass",
            "TROMBONE,     Brass",
            "TUBA,         Brass",
            "PERCUSSION,   Solo",
            "HARP,         Solo",
            "PIANO,        Solo"
    })
    void getGroup_whenCalledOnAnyInstrument_thenShouldReturnCorrectGroup(Instrument instrument, String expectedGroup) {
        // given
        // when
        String actualGroup = instrument.getGroup();

        // then
        assertThat(actualGroup).isEqualTo(expectedGroup);
    }
}