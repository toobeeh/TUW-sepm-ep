package at.ac.tuwien.sepm.assignment.individual.rest;

import java.util.List;

/**
 * Error DTO that contains multiple conflict errors
 *
 * @param message the summary of the errors
 * @param errors  the individual error details
 */
public record ConflictErrorRestDto(
    String message,
    List<String> errors
) {
}
