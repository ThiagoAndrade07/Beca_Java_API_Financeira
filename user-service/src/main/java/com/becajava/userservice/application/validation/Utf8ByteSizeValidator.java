package com.becajava.userservice.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.charset.StandardCharsets;

public class Utf8ByteSizeValidator
        implements ConstraintValidator<Utf8ByteSize, String> {

    private int maximumBytes;

    @Override
    public void initialize(Utf8ByteSize constraint) {
        maximumBytes = constraint.max();
    }

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {
        if (value == null) {
            return true;
        }

        int utf8Bytes = value.getBytes(
                StandardCharsets.UTF_8
        ).length;

        return utf8Bytes <= maximumBytes;
    }
}