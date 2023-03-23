package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;

public record HorseTreeDto(
    long id,
    String name,
    Sex sex,
    HorseTreeDto father,
    HorseTreeDto mother
) {

  HorseTreeDto withParents(HorseTreeDto newFather, HorseTreeDto newMother) {
    return new HorseTreeDto(id, name, sex, newFather, newMother);
  }
}
