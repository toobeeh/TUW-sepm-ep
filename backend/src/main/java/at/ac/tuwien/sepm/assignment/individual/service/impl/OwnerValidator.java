package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OwnerValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private void validateID(List<String> validationErrors, Long id) {
    if (id == null) {
      validationErrors.add("No ID given");
    }
  }

  private void validateName(List<String> validationErrors, String name, String nametype) {
    if (name != null) {
      if (name.isBlank()) {
        validationErrors.add("Owner " + nametype + " is given but blank");
      }
      if (name.length() > 255) {
        validationErrors.add("Owner " + nametype + " too long: longer than 255 characters");
      }
    } else {
      validationErrors.add("Owner " + nametype + " is not set");
    }
  }

  private void validateEmail(List<String> validationErrors, String email) {
    if (email != null) {
      if (email.isBlank()) {
        validationErrors.add("Owner email is given but blank");
      }
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
