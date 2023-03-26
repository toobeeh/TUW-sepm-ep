package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
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

/**
 * Validator for diverse horse data structures
 */
@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * validates a horse's id
   * - must not be null
   *
   * @param validationErrors errors to append
   * @param id               the horse's id
   */
  private void validateID(List<String> validationErrors, Long id) {
    LOG.trace("validateID({}, {})", validationErrors, id);

    if (id == null) {
      validationErrors.add("No ID given");
    }
  }

  /**
   * validates a horse's sex
   * - must not be null
   *
   * @param validationErrors errors to append
   * @param sex              the horse's sex
   */
  private void validateSex(List<String> validationErrors, Sex sex) {
    LOG.trace("validateSex({}, {})", validationErrors, sex);

    if (sex == null) {
      validationErrors.add("No sex given");
    }
  }

  /**
   * validates a horse's description
   * - must not whitespace
   * - must not be longer than 2095 chars
   *
   * @param validationErrors errors to append
   * @param description      the horse's description
   */
  private void validateDescription(List<String> validationErrors, String description) {
    LOG.trace("validateDescription({}, {})", validationErrors, description);

    if (description != null) {
      if (description.isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (description.length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }
  }

  /**
   * validates a horse's name
   * - must not be null
   * - must not whitespace
   * - must not be longer than 255 chars
   *
   * @param validationErrors errors to append
   * @param name             the horse's name
   */
  private void validateName(List<String> validationErrors, String name) {
    LOG.trace("validateName({}, {})", validationErrors, name);

    if (name != null) {
      if (name.isBlank()) {
        validationErrors.add("Horse name is given but blank");
      }
      if (name.length() > 255) {
        validationErrors.add("Horse name too long: longer than 255 characters");
      }
    } else {
      validationErrors.add("Horse name is not set");
    }
  }

  /**
   * validates a horse's birth
   * - must not be null
   * - must not be in the future
   *
   * @param validationErrors errors to append
   * @param birth            the horse's description
   */
  private void validateBirth(List<String> validationErrors, LocalDate birth) {
    LOG.trace("validateBirth({}, {})", validationErrors, birth);

    if (birth == null) {
      validationErrors.add("Horse birth is not given");
    } else if (birth.isAfter(LocalDate.now())) {
      validationErrors.add("Horse birth is in the future");
    }
  }

  /**
   * validates a horse's parents
   * - parents must not have the same id
   * - mother must be female, father must be male
   * - parents must not be younger than child
   *
   * @param validationErrors errors to append
   * @param father           the horse's father
   * @param mother           the horse's mother
   * @param childBirth       the horse's birthdate
   * @param childId          the horse's id
   */
  private void validateParents(List<String> validationErrors, HorseDetailDto father, HorseDetailDto mother, LocalDate childBirth, Long childId) {
    LOG.trace("validateParents({}, {}, {}, {}, {})", validationErrors, father, mother, childBirth, childId);

    if (mother != null && mother.id().equals(childId)) {
      validationErrors.add("Mother horse is the same as child");
    }
    if (father != null && father.id().equals(childId)) {
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

  /**
   * validates a horsedto to be updated in the persistance
   *
   * @param horse  horse update data
   * @param father the updated horse's father (not to be updated)
   * @param mother the updated horse's mother (not to be updated)
   * @throws ValidationException the horse update data was invalid
   * @throws ConflictException   the horse update data caused conflict with its parents
   */
  public void validateForUpdate(HorseDetailDto horse, HorseDetailDto father, HorseDetailDto mother) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({}, {}, {})", horse, father, mother);

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

  /**
   * validates a horsedto to be created in the persistance
   *
   * @param horse  horse create data
   * @param father the created horse's father (not to be updated)
   * @param mother the created horse's mother (not to be updated)
   * @throws ValidationException the horse create data was invalid
   * @throws ConflictException   the horse create data caused conflict with its parents
   */
  public void validateForInsert(HorseCreateDto horse, HorseDetailDto father, HorseDetailDto mother) throws ValidationException, ConflictException {
    LOG.trace("validateForInsert({}, {}, {})", horse, father, mother);

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

  /**
   * validate horse ancestor search data
   *
   * @param id          the root horse
   * @param generations the max generation hops, to be bigger than 0
   * @throws ValidationException the generation param was <= 0 or the id was null
   */
  public void validateForAncestorSearch(Long id, Long generations) throws ValidationException {
    LOG.trace("validateForAncestorSearch({}, {})", id, generations);

    List<String> validationErrors = new ArrayList<>();

    if (id == null) {
      validationErrors.add("Horse ID is not given");
    }
    if (generations == null) {
      validationErrors.add("Ancestor generations is not given");
    } else if (generations <= 0) {
      validationErrors.add("Ancestor generations have to be bigger than 1");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }

  /**
   * check for horse sex update conflicts
   *
   * @param old         the horse's sex before the update
   * @param updated     the horse's sex after the update
   * @param hasChildren indicator if the horse has any children in the db
   * @throws ConflictException the horse's gender did change, but it had already children assigned
   */
  public void validateForSexChange(Sex old, Sex updated, boolean hasChildren) throws ConflictException {
    LOG.trace("validateForSexChange({}, {}, {})", old, updated, hasChildren);

    if (!old.equals(updated) && hasChildren) {
      throw new ConflictException("Update of horse caused conflicts", List.of("Horse can't change sex, since it has children"));
    }
  }
}
