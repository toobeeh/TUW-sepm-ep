package at.ac.tuwien.sepm.assignment.individual.dto;

/**
 * DTO to bundle owner date from the database
 *
 * @param id        the owner's unique id
 * @param firstName the owner's first name
 * @param lastName  the owner's last name
 * @param email     the owner's email - optional
 */
public record OwnerDto(
    long id,
    String firstName,
    String lastName,
    String email
) {
}
