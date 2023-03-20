package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private void validateID(List<String> validationErrors, Long id) {
    if (id == null) {
      validationErrors.add("No ID given");
    }
  }

  private void validateSex(List<String> validationErrors, Sex sex) {
    if (sex == null) {
      validationErrors.add("No sex given");
    }
  }

  private void validateDescription(List<String> validationErrors, String description) {
    if (description != null) {
      if (description.isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (description.length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }
  }

  private void validateName(List<String> validationErrors, String name) {
    if (name != null) {
      if (name.isBlank()) {
        validationErrors.add("Horse name is given but blank");
      }
      if (name.length() > 255) {
        validationErrors.add("Horse name too long: longer than 255 characters");
      }
    }
  }

  private void validateBirth(List<String> validationErrors, LocalDate birth) {
    if (birth.isAfter(LocalDate.now())) {
      validationErrors.add("Horse birth is in the future");
    }
  }

  private void validateParents(List<String> validationErrors, HorseDetailDto father, HorseDetailDto mother, LocalDate childBirth, Long childId) {
    if (mother != null && mother.id() == childId) {
      validationErrors.add("Mother horse is the same as child");
    }
    if (father != null && father.id() == childId) {
      validationErrors.add("Father horse is the same as child");
    }
    if (mother != null && mother.sex() != Sex.FEMALE) {
      validationErrors.add("Mother horse is not female");
    }
    if (father != null && father.sex() != Sex.MALE) {
      validationErrors.add("Father horse is not male");
    }
    if (mother != null && mother.dateOfBirth().isAfter(childBirth)) {
      validationErrors.add("Mother horse is younger than child");
    }
    if (father != null && father.dateOfBirth().isAfter(childBirth)) {
      validationErrors.add("Father horse is younger than child");
    }
  }

  public void validateForUpdate(HorseDetailDto horse, HorseDetailDto father, HorseDetailDto mother) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();
    List<String> validationConflicts = new ArrayList<>();

    // the most reusable way since there is no polymorphism in the DTOs, yuck...
    validateID(validationErrors, horse.id());
    validateDescription(validationErrors, horse.description());
    validateSex(validationErrors, horse.sex());
    validateBirth(validationErrors, horse.dateOfBirth());
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }

    // check for conflicts if validation passed
    validateParents(validationConflicts, father, mother, horse.dateOfBirth(), horse.id());
    if (!validationConflicts.isEmpty()) {
      throw new ConflictException("Data of horse for update has conflicts", validationConflicts);
    }
  }

  public void validateForInsert(HorseCreateDto horse, HorseDetailDto father, HorseDetailDto mother) throws ValidationException, ConflictException {
    LOG.trace("validateForInsert({})", horse);
    List<String> validationErrors = new ArrayList<>();
    List<String> validationConflicts = new ArrayList<>();

    validateDescription(validationErrors, horse.description());
    validateSex(validationErrors, horse.sex());
    validateName(validationErrors, horse.name());
    validateBirth(validationErrors, horse.dateOfBirth());
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }

    // check for conflicts if validation passed
    validateParents(validationConflicts, father, mother, horse.dateOfBirth(), null);
    if (!validationConflicts.isEmpty()) {
      throw new ConflictException("Data of horse for create has conflicts", validationConflicts);
    }
  }

}
