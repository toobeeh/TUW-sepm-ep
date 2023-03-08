package at.ac.tuwien.sepm.assignment.individual.dto;

/**
 * DTO to encapsulate parameters for Owner search.
 * An owner, whose name has {@code name} as a substring is considered matched.
 *
 * @param name substring of the owner's name
 * @param maxAmount the maximum number of owners to return, even if there are more matches
 */
public record OwnerSearchDto(
    String name,
    Integer maxAmount // needs to be present always
) {
}
