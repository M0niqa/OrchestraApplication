package com.monika.worek.orchestra.model;

import lombok.Getter;

@Getter
public enum TaxOffice {
    US_WARSZAWA_MOKOTOW("Urząd Skarbowy Warszawa-Mokotów"),
    US_WARSZAWA_SRODMIESCIE("Urząd Skarbowy Warszawa-Śródmieście"),
    US_WARSZAWA_WOLA("Urząd Skarbowy Warszawa-Wola"),
    US_KRAKOW_PODGORZE("Urząd Skarbowy Kraków-Podgórze"),
    US_KRAKOW_NOWA_HUTA("Urząd Skarbowy Kraków-Nowa Huta"),
    US_POZNAN_WINOGRADY("Urząd Skarbowy Poznań-Winogrady"),
    US_WROCLAW_KRZYKI("Urząd Skarbowy Wrocław-Krzyki"),
    US_GDANSK_OLIWA("Urząd Skarbowy Gdańsk-Oliwa"),
    US_KATOWICE_SRUBIARNIA("Urząd Skarbowy Katowice-Śrubarnia");

    private final String displayName;

    TaxOffice(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    }
