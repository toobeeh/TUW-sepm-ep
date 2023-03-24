package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.time.LocalDate;

/**
 * DTO which contains a horse. Mother and father are omitted.
 *
 * @param name        a string without restrictions for the horse name
 * @param description a string without restriction for the horse description
 * @param dateOfBirth the date of the horses birth. has to be younger than its parents
 * @param sex         the sex of the horse
 * @param owner       the owner of the horse
 */
public record HorseDetailDto(
    Long id,
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    OwnerDto owner
) {
}
