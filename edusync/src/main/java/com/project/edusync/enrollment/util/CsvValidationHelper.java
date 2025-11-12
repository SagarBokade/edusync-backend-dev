package com.project.edusync.enrollment.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * A stateless Spring component providing utility methods for validating and
 * parsing data from CSV rows during bulk import operations.
 *
 * Each method is designed to be atomic and to throw a specific
 * {@link IllegalArgumentException} upon failure, which can be caught by the
 * calling service to generate a per-row error report.
 */
@Component
@Slf4j
public class CsvValidationHelper {

    /**
     * A simple, common email validation regex.
     * While not 100% RFC 5322 compliant, it's sufficient for 99.9%
     * of practical use cases and prevents malformed data.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
    );

    /**
     * A common date formatter. Adjust this if your CSVs use a different format
     * (e.g., "dd/MM/yyyy"). "yyyy-MM-dd" is the ISO standard.
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // e.g., "2025-11-11"

    /**
     * Validates that a required string field is not null or blank.
     *
     * @param value The string value from the CSV cell.
     * @param fieldName The name of the field (e.g., "firstName") for error messages.
     * @return The original, trimmed value if valid.
     * @throws IllegalArgumentException if the string is blank or null.
     */
    public String validateString(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
        }
        return value.trim();
    }

    /**
     * Validates a string as a proper email address.
     *
     * @param email The email string to validate.
     * @return The original, trimmed email if valid.
     * @throws IllegalArgumentException if the email is blank or has an invalid format.
     */
    public String validateEmail(String email) {
        String validatedEmail = validateString(email, "email");
        if (!EMAIL_PATTERN.matcher(validatedEmail).matches()) {
            throw new IllegalArgumentException("Invalid email format: '" + validatedEmail + "'.");
        }
        return validatedEmail;
    }

    /**
     * Parses a string into a {@link LocalDate}.
     *
     * @param dateStr The date string from the CSV.
     * @param fieldName The name of the field for error messages.
     * @return The parsed {@link LocalDate}.
     * @throws IllegalArgumentException if the string is blank or not a valid date.
     */
    public LocalDate parseDate(String dateStr, String fieldName) {
        String trimmedDate = validateString(dateStr, fieldName);
        try {
            return LocalDate.parse(trimmedDate, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse date string '{}' for field '{}'", trimmedDate, fieldName);
            throw new IllegalArgumentException(
                    "Invalid date format for " + fieldName + ". Expected format: yyyy-MM-dd."
            );
        }
    }

    /**
     * Parses a string into an {@link Integer}.
     *
     * @param intStr The integer string from the CSV.
     * @param fieldName The name of the field for error messages.
     * @return The parsed {@link Integer}.
     * @throws IllegalArgumentException if the string is blank or not a valid integer.
     */
    public Integer parseInt(String intStr, String fieldName) {
        String trimmedInt = validateString(intStr, fieldName);
        try {
            return Integer.parseInt(trimmedInt);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse integer string '{}' for field '{}'", trimmedInt, fieldName);
            throw new IllegalArgumentException(
                    fieldName + " is not a valid number: '" + trimmedInt + "'."
            );
        }
    }

    /**
     * Parses a string into a {@link Boolean}.
     * Handles "true", "false" (case-insensitive), "1", "0", "yes", "no".
     *
     * @param boolStr The boolean string from the CSV.
     * @param fieldName The name of the field for error messages.
     * @return The parsed {@link Boolean}.
     * @throws IllegalArgumentException if the string is blank or not a valid boolean.
     */
    public Boolean parseBoolean(String boolStr, String fieldName) {
        String validatedStr = validateString(boolStr, fieldName).toLowerCase();

        if ("true".equals(validatedStr) || "1".equals(validatedStr) || "yes".equals(validatedStr)) {
            return true;
        }
        if ("false".equals(validatedStr) || "0".equals(validatedStr) || "no".equals(validatedStr)) {
            return false;
        }

        throw new IllegalArgumentException(
                "Invalid boolean value for " + fieldName + ": '" + boolStr + "'. Expected true/false, 1/0, or yes/no."
        );
    }

    /**
     * A generic, case-insensitive parser for any Enum class.
     *
     * @param <E> The Enum type (e.g., Gender.class, StaffType.class).
     * @param enumClass The .class of the Enum to parse into.
     * @param value The string value from the CSV.
     * @param fieldName The name of the field for error messages.
     * @return The matching Enum constant.
     * @throws IllegalArgumentException if the string is blank or no matching enum constant
     * is found.
     */
    public <E extends Enum<E>> E parseEnum(Class<E> enumClass, String value, String fieldName) {
        String validatedValue = validateString(value, fieldName);
        try {
            // This is the standard, case-insensitive way to find an enum
            return E.valueOf(enumClass, validatedValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Failed to parse enum {} for field '{}' with value '{}'",
                    enumClass.getSimpleName(), fieldName, validatedValue);
            throw new IllegalArgumentException(
                    "Invalid value for " + fieldName + ": '" + validatedValue + "'."
            );
        }
    }
}