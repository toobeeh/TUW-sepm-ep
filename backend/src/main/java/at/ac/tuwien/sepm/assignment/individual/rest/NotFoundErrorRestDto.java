package at.ac.tuwien.sepm.assignment.individual.rest;

/**
 * Error DTO to contain a not found exception
 *
 * @param message the error summary
 */
public record NotFoundErrorRestDto(
    String message
) {
}
