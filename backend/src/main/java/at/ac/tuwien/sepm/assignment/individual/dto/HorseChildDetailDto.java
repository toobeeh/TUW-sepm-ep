package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.time.LocalDate;

/**
 * DTO which bundles all available horse data.
 * Includes the parent's horses; but those without their parents.
 *
 * @param id          the horse's id
 * @param name        the horse's name
 * @param description the horse's description
 * @param dateOfBirth the horse's date of birth
 * @param sex         the horse's sex
 * @param owner       the horse's owner
 * @param father      the horse's father, as {@link HorseDetailDto}
 * @param mother      the horse's mother, as {@link HorseDetailDto}
 */
public record HorseChildDetailDto(
    Long id,
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    OwnerDto owner,
    HorseDetailDto father,
    HorseDetailDto mother
) {
  /**
   * Returns a copy of this DTO, but with an id assigned
   *
   * @param newId new id of this horse
   * @return copy of this dto with a set new id
   */
  public HorseChildDetailDto withId(long newId) {
    return new HorseChildDetailDto(
        newId,
        name,
        description,
        dateOfBirth,
        sex,
        owner,
        father,
        mother);
  }

  /**
   * Returns a copy of this DTO, but without parents
   *
   * @return a copy without parents
   */
  public HorseDetailDto withoutParents() {
    return new HorseDetailDto(
        id,
        name,
        description,
        dateOfBirth,
        sex,
        owner
    );
  }

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
