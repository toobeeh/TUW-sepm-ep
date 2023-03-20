package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final OwnerService ownerService;

  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator validator, OwnerService ownerService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.ownerService = ownerService;
  }

  @Override
  public Stream<HorseListDto> allHorses() {
    LOG.trace("allHorses()");
    var horses = dao.getAll();
    var ownerIds = horses.stream()
        .map(Horse::getOwnerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, ownerMap));
  }


  @Override
  public HorseChildDetailDto update(HorseChildDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);

    // get id from parents and fetch stored entity for validation
    var simpleHorse = horse.withoutParents();
    var father = horse.father() == null ? null : dao.getById(horse.father().id());
    var mother = horse.mother() == null ? null : dao.getById(horse.mother().id());
    validator.validateForUpdate(simpleHorse, father, mother);
    var updatedHorse = dao.update(simpleHorse);

    return mapper.entityToChildDetailDto(
        updatedHorse,
        ownerMapForSingleId(updatedHorse.getOwnerId()),
        father,
        mother);
  }

  @Override
  public HorseChildDetailDto create(HorseCreateDto horse) throws ValidationException, ConflictException, NotFoundException {
    LOG.trace("create({})", horse);

    var father = horse.fatherId() == null ? null : dao.getById(horse.fatherId());
    var mother = horse.motherId() == null ? null : dao.getById(horse.motherId());
    validator.validateForInsert(horse, father, mother);

    var createdHorse = dao.create(horse);

    return mapper.entityToChildDetailDto(
        createdHorse,
        ownerMapForSingleId(createdHorse.getOwnerId()),
        father,
        mother
    );
  }

  @Override
  public HorseChildDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);

    Horse horse = dao.getById(id);
    var father = horse.getFatherId() == null ? null : dao.getById(horse.getFatherId());
    var mother = horse.getMotherId() == null ? null : dao.getById(horse.getMotherId());

    return mapper.entityToChildDetailDto(
        horse,
        ownerMapForSingleId(horse.getOwnerId()),
        father,
        mother);
  }

  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    dao.delete(id);
  }


  private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) {
    try {
      return ownerId == null
          ? null
          : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
    } catch (NotFoundException e) {
      throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId));
    }
  }

}
