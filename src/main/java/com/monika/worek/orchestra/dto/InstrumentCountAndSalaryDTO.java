package com.monika.worek.orchestra.dto;

import com.monika.worek.orchestra.model.Instrument;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class InstrumentCountAndSalaryDTO {
    private Map<Instrument, Integer> instrumentCounts = new EnumMap<>(Instrument.class);
    private Map<String, BigDecimal> groupSalaries = new HashMap<>();
}
