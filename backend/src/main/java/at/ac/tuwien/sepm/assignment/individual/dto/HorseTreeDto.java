package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.time.LocalDate;

public record HorseTreeDto(
    long id,
    String name,
    Sex sex,
    LocalDate dateOfBirth,
    HorseTreeDto father,
    HorseTreeDto mother
) {

  HorseTreeDto withParents(HorseTreeDto newFather, HorseTreeDto newMother) {
    return new HorseTreeDto(id, name, sex, dateOfBirth, newFather, newMother);
  }
}
