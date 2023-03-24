package at.ac.tuwien.sepm.assignment.individual.rest;

import java.util.List;

/**
 * Error DTO to contain a not ound exception
 *
 * @param message the error summary
 */
public record NotFoundErrorRestDto(
    String message
) {
}
