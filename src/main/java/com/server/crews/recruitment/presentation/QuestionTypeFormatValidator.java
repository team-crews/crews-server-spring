package com.server.crews.recruitment.presentation;

import com.server.crews.recruitment.domain.QuestionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class QuestionTypeFormatValidator implements ConstraintValidator<QuestionTypeFormat, String> {

    @Override
    public void initialize(QuestionTypeFormat constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        List<String> questionTypeNames = Arrays.stream(QuestionType.values())
                .map(QuestionType::name)
                .toList();
        return questionTypeNames.stream()
                .anyMatch(questionTypeName -> isConvertible(questionTypeName, value));
    }

    public boolean isConvertible(String questionTypeName, String value) {
        return value.trim().equalsIgnoreCase(questionTypeName);
    }
}
