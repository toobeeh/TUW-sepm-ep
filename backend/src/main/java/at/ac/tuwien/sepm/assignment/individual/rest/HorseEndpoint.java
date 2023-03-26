package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseGenerationsDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";
  private final HorseService service;

  /**
   * A controller for the /horses route
   *
   * @param service Horse service to process in- and outgoing data
   */
  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  /**
   * Searches for horses that match given search criteria.
   *
   * @param searchParameters Query parameters for search filters. Provide none to get all horses
   * @return a list of horses with their common properties; without parent information
   */
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Stream<HorseDetailDto> search(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("Request query parameters: {}", searchParameters);
    return service.searchHorses(searchParameters);
  }

  /**
   * Gets a horse by its id
   *
   * @param id the id of the horse to get
   * @return the horse with its parents, if set. The parents don't include their parents
   * @throws NotFoundException the horse id could not be found in the database
   */
  @GetMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public HorseChildDetailDto getById(@PathVariable long id) throws NotFoundException {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    return service.getById(id);
  }

  /**
   * Gets the ancestors of a horse up to the n-th generation
   *
   * @param id                   the id of the horse where to start the family tree
   * @param generationParameters the maximum number of generations starting from the child; bigger than 0
   * @return the horse with its parents. The parents include their parents, until there are no more or the generation limit has been reached
   * @throws NotFoundException   the horse id could not be found in the database
   * @throws ValidationException input parameters like generation were invalid
   */
  @GetMapping("ancestors/{id}")
  @ResponseStatus(HttpStatus.OK)
  public HorseTreeDto getAncestors(@PathVariable Long id, HorseGenerationsDto generationParameters) throws NotFoundException, ValidationException {
    LOG.info("GET " + BASE_PATH + "/ancestors/{}", id);
    LOG.debug("Request query parameters: {}", generationParameters);
    return service.getAncestors(id, generationParameters.generations());
  }

  /**
   * Updates the horse with provided data
   *
   * @param id       the id of the horse to update
   * @param toUpdate the new data for this horse
   * @return the updated horse
   * @throws ValidationException the provided data for the update was invalid
   * @throws ConflictException   the provided data for the update caused conflicts with other horses or owners
   * @throws NotFoundException   the horse to update or its owner could not be found
   */
  @PutMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public HorseChildDetailDto update(@PathVariable long id, @RequestBody HorseChildDetailDto toUpdate)
      throws ValidationException, ConflictException, NotFoundException {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Request body:\n{}", toUpdate);
    return service.update(toUpdate.withId(id));
  }

  /**
   * Creates a new horse with provided data
   *
   * @param toCreate the data of the horse to create
   * @return the created horse
   * @throws ConflictException   the provided data for the new horse caused conflicts with other horses or owners
   * @throws ValidationException the provided data for the new horse was invalid
   * @throws NotFoundException   the provided owner of the horse could not be found
   */
  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public HorseChildDetailDto create(@RequestBody HorseCreateDto toCreate) throws ConflictException, ValidationException, NotFoundException {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Request body:\n{}", toCreate);
    return service.create(toCreate);
  }

  /**
   * Deletes a horse from the database
   *
   * @param id the id of the horse to delete
   * @throws NotFoundException the horse to delete could not be found
   */
  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) throws NotFoundException {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);
    service.delete(id);
  }
}
