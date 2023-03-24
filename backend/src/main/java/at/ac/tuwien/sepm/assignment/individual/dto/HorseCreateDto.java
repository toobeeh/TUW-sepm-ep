package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.time.LocalDate;

/**
 * DTO to create a new horse. Owner, mother, father and description are optional.
 *
 * @param name        a string without restrictions for the horse name
 * @param description a string without restriction for the horse description
 * @param dateOfBirth the date of the horses birth. has to be younger than its parents
 * @param sex         the sex of the horse
 * @param owner       the owner of the horse
 * @param father      the father of the horse
 * @param mother      the mother of the horse
 */
public record HorseCreateDto(
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    OwnerDto owner,
    HorseDetailDto father,
    HorseDetailDto mother
) {

  /**
   * Gets the id of the owner
   *
   * @return null if the owner is null, else their id
   */
  public Long ownerId() {
    return owner == null
        ? null
        : owner.id();
  }

  /**
   * Gets the id of the father
   *
   * @return null if the father is null, else their id
   */
  public Long fatherId() {
    return father == null
        ? null
        : father.id();
  }

  /**
   * Gets the id of the mother
   *
   * @return null if the mother is null, else their id
   */
  public Long motherId() {
    return mother == null
        ? null
        : mother.id();
  }
}
