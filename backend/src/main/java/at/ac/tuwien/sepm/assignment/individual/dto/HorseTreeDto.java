package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.time.LocalDate;

/**
 * DTO to bundle an ancestor family tree of a horse
 *
 * @param id          the id of the child horse
 * @param name        the name of the child horse
 * @param sex         the sex of the child horse
 * @param dateOfBirth the date of birth of the child horse
 * @param father      the horse's father and its ancestors
 * @param mother      the horse's mother and its ancestors
 */
public record HorseTreeDto(
    long id,
    String name,
    Sex sex,
    LocalDate dateOfBirth,
    HorseTreeDto father,
    HorseTreeDto mother
) {
}
