package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.time.LocalDate;

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
  public HorseChildDetailDto withId(long newId) {
    return new HorseChildDetailDto(
        newId,
        name,
        description,
        dateOfBirth,
        sex,
        owner,
        mother,
        father);
  }

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

  public Long ownerId() {
    return owner == null
        ? null
        : owner.id();
  }
}
