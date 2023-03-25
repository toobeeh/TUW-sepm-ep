package at.ac.tuwien.sepm.assignment.individual.dto;

/**
 * DTO to bundle the query parameters used in searching horses.
 * Each field can be null, in which case this field is not filtered by.
 *
 * @param generations The maximum distance of "generation-hops" from the child to its farthest ancestor
 */
public record HorseGenerationsDto(
    Integer generations
) {
}
