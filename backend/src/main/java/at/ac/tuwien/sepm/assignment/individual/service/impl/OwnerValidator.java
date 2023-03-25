package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for diverse owner data structures
 */
@Component
public class OwnerValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * validates an owner's name
   * - must not be null
   * - must not whitespace
   * - must not be longer than 255 chars
   *
   * @param validationErrors errors to append
   * @param name             the owner's name
   * @param nameType         first/last name
   */
  private void validateName(List<String> validationErrors, String name, String nameType) {
    LOG.trace("validateName({}, {}, {})", validationErrors, name, nameType);

    if (name != null) {
      if (name.isBlank()) {
        validationErrors.add("Owner " + nameType + " is given but blank");
      }
      if (name.length() > 255) {
        validationErrors.add("Owner " + nameType + " too long: longer than 255 characters");
      }
    } else {
      validationErrors.add("Owner " + nameType + " is not set");
    }
  }

  /**
   * validates an owner's email
   * - must not be null
   * - must not be longer than 255 chars
   * - must match a common email regex
   *
   * @param validationErrors errors to append
   * @param email            the owner's email
   */
  private void validateEmail(List<String> validationErrors, String email) {
    LOG.trace("validateEmail({}, {})", validationErrors, email);

    if (email != null) {
      if (email.length() > 255) {
        validationErrors.add("Owner email too long: longer than 255 characters");
      }

      String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
      Pattern pattern = Pattern.compile(emailRegex);
      Matcher matcher = pattern.matcher(email);

      if (!matcher.matches()) {
        validationErrors.add("Owner email is not in a valid format");
      }
    } else {
      validationErrors.add("Owner email is not set");
    }
  }

  /**
   * validates owner create data for an insert in the persistence
   *
   * @param owner the owner create data
   * @throws ValidationException owner data did not comply all the validations
   */
  public void validateForInsert(OwnerCreateDto owner) throws ValidationException {
    LOG.trace("validateForInsert({})", owner);

    List<String> validationErrors = new ArrayList<>();

    validateName(validationErrors, owner.firstName(), "firstname");
    validateName(validationErrors, owner.lastName(), "lastname");
    validateEmail(validationErrors, owner.email());
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of owner for create failed", validationErrors);
    }
  }

}
