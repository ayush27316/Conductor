package com.conductor.core.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValueEvaluator implements ConstraintValidator<EnumValue, CharSequence> {
    private List<String> acceptedValues;
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValue annotation) {
        this.enumClass = annotation.enumClass();
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean isValid = acceptedValues.contains(value.toString());

        // If invalid and using default message, create dynamic message
        if (!isValid && context.getDefaultConstraintMessageTemplate().equals("must be any of enum {enumClass}")) {
            context.disableDefaultConstraintViolation();

            String availableOptions = getAvailableOptions();
            String dynamicMessage = "Invalid option. Available options are: " + availableOptions;

            context.buildConstraintViolationWithTemplate(dynamicMessage)
                    .addConstraintViolation();
        }

        return isValid;
    }

    private String getAvailableOptions() {
        try {
            // Try to call getAllOptions() method if it exists
            Method getAllOptionsMethod = enumClass.getMethod("getAllOptions");
            return (String) getAllOptionsMethod.invoke(null);
        } catch (Exception e) {
            // Fallback to enum constant names if getAllOptions() doesn't exist
            return Stream.of(enumClass.getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
        }
    }
}
