package com.monika.worek.orchestra.constraint;

import com.monika.worek.orchestra.dto.ProjectBasicInfoDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StartDateBeforeEndDateValidator implements ConstraintValidator<StartDateBeforeEndDate, ProjectBasicInfoDTO> {

    @Override
    public void initialize(StartDateBeforeEndDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(ProjectBasicInfoDTO projectDto, ConstraintValidatorContext context) {
        if (projectDto.getStartDate() == null || projectDto.getEndDate() == null) {
            return true;
        }

        return !projectDto.getStartDate().isAfter(projectDto.getEndDate());
    }
}
