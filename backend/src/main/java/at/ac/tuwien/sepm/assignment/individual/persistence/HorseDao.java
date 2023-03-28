package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {

  /**
   * Get all horses stored in the persistent data store that match search criteria.
   *
   * @return a list of all stored horses that match the search filter
   */
  List<Horse> searchAll(HorseSearchDto searchFilter);

  /**
   * Get all horses that are ancestors of at max the nth generation of a horse.
   *
   * @param generations the maximum amount of generation-steps, must not be negative
   * @param rootId      the id of the horse with generation 0
   * @return a list of all stored horses that are ancestors
   * @throws NotFoundException the root horse could not be found
   */
  List<Horse> getAncestors(long rootId, long generations) throws NotFoundException;


  /**
   * Checks if a horse has children that are older than it
   *
   * @param parentId    the parent horse's id
   * @param parentBirth the parent horse's birthdate
   * @return true if there are horses that have the parent horse as father or mother and are older than given date
   */
  boolean hasOlderChildren(long parentId, LocalDate parentBirth);

  /**
   * Check if there is a horse that is a parent with given id
   * (other horses reference this horse as father or mother)
   *
   * @param horseId the horse to check for
   * @return false if no horse has this id as parent, or there is no horse with such id at all
   */
  boolean isParent(long horseId);

  /**
   * Update the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse update(HorseChildDetailDto horse) throws NotFoundException;


  /**
   * Inserts the horse with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to create
   * @return the created horse
   */
  Horse create(HorseCreateDto horse);

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Delete a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to delete
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  void delete(long id) throws NotFoundException;
}
