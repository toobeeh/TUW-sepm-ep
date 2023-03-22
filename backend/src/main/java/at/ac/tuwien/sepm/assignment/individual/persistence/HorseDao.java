package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;

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
