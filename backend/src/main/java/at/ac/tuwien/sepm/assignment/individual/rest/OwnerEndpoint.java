package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(OwnerEndpoint.BASE_PATH)
public class OwnerEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/owners";
  private final OwnerService service;

  /**
   * A controller for the /owners route
   *
   * @param service Owner service to process in- and outgoing data
   */
  public OwnerEndpoint(OwnerService service) {
    this.service = service;
  }

  /**
   * Searches for owners  that match given search criteria.
   *
   * @param searchParameters Query parameters for search filters. Provide none to get all owners
   * @return a list of owners with their common properties
   */
  @GetMapping
  public Stream<OwnerDto> search(OwnerSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("Request query parameters: {}", searchParameters);
    return service.search(searchParameters);
  }

  /**
   * Creates a new owner with provided data
   *
   * @param toCreate the data of the owner to create
   * @return the created owner
   * @throws ValidationException the provided data for the new owner was invalid
   */
  @PostMapping()
  public OwnerDto create(@RequestBody OwnerCreateDto toCreate) throws ValidationException, ConflictException {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Request body:\n{}", toCreate);
    return service.create(toCreate);
  }
}
