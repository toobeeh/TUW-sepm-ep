package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {

  /**
   * Searches for all horses in the system that match a given filter.
   *
   * @param search the horse filter parameters
   * @return list of all stored horses that match the criteria
   */
  Stream<HorseDetailDto> searchHorses(HorseSearchDto search);

  /**
   * Get all horses that are ancestors of at max the nth generation of a horse.
   *
   * @param rootId
   * @param generations
   * @return a dto that contains a tree of the horses ancestors
   * @throws NotFoundException   the provided root horse did not exist in the database
   * @throws ValidationException the provided generations parameter was invalid
   */
  HorseTreeDto getAncestors(long rootId, long generations) throws NotFoundException, ValidationException;

  /**
   * Updates the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException   if the horse with given ID does not exist in the persistent data store
   * @throws ValidationException if the update data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException   if the update data given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  HorseChildDetailDto update(HorseChildDetailDto horse) throws NotFoundException, ValidationException, ConflictException;

  /**
   * Creates the horse with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to create
   * @return the created horse
   * @throws ValidationException if the creation data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException   if the update data given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  HorseChildDetailDto create(HorseCreateDto horse) throws ValidationException, ConflictException, NotFoundException;

  /**
   * Get the horse with given ID, with more detail information.
   * This includes the owner of the horse, and its parents.
   * The parents of the parents are not included.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  HorseChildDetailDto getById(long id) throws NotFoundException;

  /**
   * Deletes the horse with given ID.
   *
   * @param id the ID of the horse to delete
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  void delete(long id) throws NotFoundException;
}
