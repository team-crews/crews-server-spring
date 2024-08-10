package com.server.crews.recruitment.presentation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = QuestionTypeFormatValidator.class)
public @interface QuestionTypeFormat {

    String message() default "유효하지 않은 질문 유형(QuestionType) 값입니다. NARRATIVE 혹은 SELECTIVE를 입력해주세요. (대소문자 무관)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
