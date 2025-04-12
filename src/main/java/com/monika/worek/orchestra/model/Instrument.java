package com.monika.worek.orchestra.model;

public enum Instrument {
    VIOLIN("Violin"),
    VIOLA("Viola"),
    CELLO("Cello"),
    DOUBLE_BASS("Double bass"),
    HARP("Harp"),
    FLUTE("Flute"),
    OBOE("Oboe"),
    CLARINET("Clarinet"),
    BASSOON("Bassoon"),
    TRUMPET("Trumpet"),
    FRENCH_HORN("French horn"),
    TROMBONE("Trombone"),
    TUBA("Tuba"),
    PERCUSSION("Percussion"),
    PIANO("Piano");

    private final String displayName;

    Instrument(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
