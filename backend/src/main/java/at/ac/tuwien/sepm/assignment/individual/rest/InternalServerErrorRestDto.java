package at.ac.tuwien.sepm.assignment.individual.rest;

/**
 * Error DTO to contain a fatal exception
 *
 * @param message the error summary
 */
public record InternalServerErrorRestDto(
    String message
) {
}
