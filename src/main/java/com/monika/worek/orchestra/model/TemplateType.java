package com.monika.worek.orchestra.model;

public enum TemplateType {
    CONCERT("Concert"),
    TOUR("Tour"),
    RECORDING("Recording");

    private final String displayName;

    TemplateType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
