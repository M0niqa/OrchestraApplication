package com.monika.worek.orchestra.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PeselValidator implements ConstraintValidator<PESEL, String> {

    @Override
    public void initialize(PESEL constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return isValidPesel(value);
    }

    private boolean isValidPesel(String pesel) {
        if (!pesel.matches("\\d{11}")) {
            return false;
        }

        String[] arrPesel = pesel.split("");
        int sum = 0;
        for (int i = 0; i < arrPesel.length-1; i++) {
            sum = sum + Integer.parseInt(arrPesel[i]) * getMultiplier(i+1);
        }
        int modulo = sum%10;
        int lastDigit = Character.getNumericValue(pesel.charAt(10));

        return ((modulo == 0 && lastDigit == 0) || lastDigit == 10 - modulo);
    }

    private static int getMultiplier(int index) {
        return switch (index % 4) {
            case 2 -> 3;
            case 3 -> 7;
            case 0 -> 9;
            default -> 1;
        };
    }
}