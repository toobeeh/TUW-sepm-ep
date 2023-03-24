package at.ac.tuwien.sepm.assignment.individual.rest;

import java.util.List;

/**
 * Error DTO that contains multiple validation errors
 *
 * @param message the summary of the errors
 * @param errors  the individual error details
 */
public record ValidationErrorRestDto(
    String message,
    List<String> errors
) {
}
