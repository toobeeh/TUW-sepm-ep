package at.ac.tuwien.sepm.assignment.individual.dto;

/**
 * DTO to bundle data used to create a new owner
 *
 * @param firstName the first name of the new owner
 * @param lastName  the last name of the new owner
 * @param email     the email of the new owner - optional
 */
public record OwnerCreateDto(
    String firstName,
    String lastName,
    String email
) {
}
