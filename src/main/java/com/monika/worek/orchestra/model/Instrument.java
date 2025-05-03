package com.monika.worek.orchestra.model;

import lombok.Getter;

@Getter
public enum Instrument {
    VIOLIN_I("Violin I"),
    VIOLIN_II("Violin II"),
    VIOLA("Viola"),
    CELLO("Cello"),
    DOUBLE_BASS("Double bass"),
    FLUTE("Flute"),
    OBOE("Oboe"),
    CLARINET("Clarinet"),
    BASSOON("Bassoon"),
    TRUMPET("Trumpet"),
    FRENCH_HORN("French horn"),
    TROMBONE("Trombone"),
    TUBA("Tuba"),
    PERCUSSION("Percussion"),
    HARP("Harp"),
    PIANO("Piano");

    public String getGroup() {
        return switch (this) {
            case VIOLIN_I, VIOLIN_II, VIOLA, CELLO, DOUBLE_BASS -> "Strings";
            case FLUTE, OBOE, CLARINET, BASSOON -> "Winds";
            case TRUMPET, FRENCH_HORN, TROMBONE, TUBA -> "Brass";
            case HARP, PERCUSSION, PIANO -> "Solo";
        };
    }

    private final String displayName;

    Instrument(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

}
