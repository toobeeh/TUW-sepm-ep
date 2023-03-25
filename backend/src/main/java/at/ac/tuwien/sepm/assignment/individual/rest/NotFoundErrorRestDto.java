package at.ac.tuwien.sepm.assignment.individual.rest;

/**
 * Error DTO to contain a not ound exception
 *
 * @param message the error summary
 */
public record NotFoundErrorRestDto(
    String message
) {
}
