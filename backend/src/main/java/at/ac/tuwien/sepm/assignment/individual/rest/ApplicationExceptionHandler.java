package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Exception handler for validation exceptions
   *
   * @param e the caught validation exception
   * @return a dto with an appropriate message for the frontend
   */
  @ExceptionHandler
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ResponseBody
  public ValidationErrorRestDto handleValidationException(ValidationException e) {
    LOG.warn("Terminating request processing with status 422 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
    return new ValidationErrorRestDto(e.summary(), e.errors());
  }

  /**
   * Exception handler for not found exceptions
   *
   * @param e the caught not found exception
   * @return a dto with an appropriate message for the frontend
   */
  @ExceptionHandler
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public NotFoundErrorRestDto handleNotFoundException(NotFoundException e) {
    LOG.warn("Terminating request processing with status 404 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
    return new NotFoundErrorRestDto(e.getMessage());
  }

  /**
   * Exception handler for conflict exceptions
   *
   * @param e the caught conflict exception
   * @return a dto with an appropriate message for the frontend
   */
  @ExceptionHandler
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  public ConflictErrorRestDto handleConflictException(ConflictException e) {
    LOG.warn("Terminating request processing with status 409 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
    return new ConflictErrorRestDto(e.summary(), e.errors());
  }
}
