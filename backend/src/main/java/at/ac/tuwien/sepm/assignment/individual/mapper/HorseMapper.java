package at.ac.tuwien.sepm.assignment.individual.mapper;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public HorseMapper() {
  }

  /**
   * Convert a horse entity object to a {@link HorseListDto}.
   * The given map of owners needs to contain the owner of {@code horse}.
   *
   * @param horse  the horse to convert
   * @param owners a map of horse owners by their id, which needs to contain the owner referenced by {@code horse}
   * @return the converted {@link HorseListDto}
   */
  public HorseListDto entityToListDto(Horse horse, Map<Long, OwnerDto> owners) {
    LOG.trace("entityToDto({})", horse);
    if (horse == null) {
      return null;
    }

    return new HorseListDto(
        horse.getId(),
        horse.getName(),
        horse.getDescription(),
        horse.getDateOfBirth(),
        horse.getSex(),
        getOwner(horse, owners)
    );
  }

  /**
   * Convert a horse entity object to a {@link HorseTreeDto} and include all its ancestors from a pool.
   *
   * @param root the horse at the bottom of the family tree
   * @param pool a pool of possible ancestor horses. to find ancestors of the parents, the parents are not removed since the family tree could contain more complex inheritance lines.
   * @return the converted {@link HorseTreeDto}
   */
  public HorseTreeDto findAncestors(Horse root, Supplier<Stream<Horse>> pool) {
    var father = pool.get().filter(hors -> hors.getId() == root.getFatherId()).findFirst();
    var mother = pool.get().filter(hors -> hors.getId() == root.getMotherId()).findFirst();

    var fatherDto = father.isEmpty() ? null : findAncestors(father.get(), pool);
    var motherDto = mother.isEmpty() ? null : findAncestors(mother.get(), pool);

    return new HorseTreeDto(
        root.getId(),
        root.getName(),
        root.getSex(),
        root.getDateOfBirth(),
        fatherDto,
        motherDto
    );
  }

  /**
   * Convert a horse entity object to a {@link HorseListDto}.
   * The given map of owners needs to contain the owner of {@code horse}.
   *
   * @param horse  the horse to convert
   * @param owners a map of horse owners by their id, which needs to contain the owner referenced by {@code horse}
   * @return the converted {@link HorseListDto}
   */
  public HorseDetailDto entityToDetailDto(
      Horse horse,
      Map<Long, OwnerDto> owners) {
    LOG.trace("entityToDetailDto({})", horse);
    if (horse == null) {
      return null;
    }


    return new HorseDetailDto(
        horse.getId(),
        horse.getName(),
        horse.getDescription(),
        horse.getDateOfBirth(),
        horse.getSex(),
        getOwner(horse, owners)
    );
  }

  /**
   * Convert a horse entity object to a {@link HorseChildDetailDto}.
   * The given map of owners needs to contain the owner of {@code horse}.
   *
   * @param horse  the horse to convert
   * @param owners a map of horse owners by their id, which needs to contain the owner referenced by {@code horse}
   * @param father the father entity of the horse; gets mapped to a {@link HorseDetailDto}
   * @param mother the father entity of the horse; gets mapped to a {@link HorseDetailDto}
   * @return the converted {@link HorseListDto}
   */
  public HorseChildDetailDto entityToChildDetailDto(
      Horse horse,
      Map<Long, OwnerDto> owners,
      Horse father,
      Horse mother) {
    LOG.trace("entityToChildDetailDto({})", horse);
    if (horse == null) {
      return null;
    }

    return new HorseChildDetailDto(
        horse.getId(),
        horse.getName(),
        horse.getDescription(),
        horse.getDateOfBirth(),
        horse.getSex(),
        getOwner(horse, owners),
        father == null ? null : entityToDetailDto(father, owners),
        mother == null ? null : entityToDetailDto(mother, owners)
    );
  }

  private OwnerDto getOwner(Horse horse, Map<Long, OwnerDto> owners) {
    OwnerDto owner = null;
    var ownerId = horse.getOwnerId();
    if (ownerId != null) {
      if (!owners.containsKey(ownerId)) {
        throw new FatalException("Given owner map does not contain owner of this Horse (%d)".formatted(horse.getId()));
      }
      owner = owners.get(ownerId);
    }
    return owner;
  }

}
