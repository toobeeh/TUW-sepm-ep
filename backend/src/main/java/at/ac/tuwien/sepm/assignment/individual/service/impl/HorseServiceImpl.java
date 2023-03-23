package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseTreeDto;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
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
  public Stream<HorseListDto> searchHorses(HorseSearchDto search) {
    LOG.trace("searchHorses()");

    // search need not be validated: sex & date validated by parser;

    var horses = dao.searchAll(search);
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
  public HorseTreeDto getAncestors(long rootId, long generations) throws NotFoundException {
    LOG.trace("getAncestors({},{})", rootId, generations);

    /* TODO: validate params */

    // map horses and get root horse
    var ancestors = dao.getAncestors(rootId, generations);
    Supplier<Stream<Horse>> pool = () -> ancestors.stream();
    var root = pool.get().filter(hors -> hors.getId() == rootId).findFirst();
    if (root.isEmpty()) {
      throw new FatalException("Horse ancestors didnt include horse itself");
    }

    return mapper.findAncestors(root.get(), pool);
  }


  @Override
  public HorseChildDetailDto update(HorseChildDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);

    // get IDs of parents, fetch entity from db and convert to DTO for validator
    var simpleHorse = horse.withoutParents();
    var father = horse.father() == null ? null : dao.getById(horse.father().id());
    var mother = horse.mother() == null ? null : dao.getById(horse.mother().id());
    var fatherDto = father == null ? null : mapper.entityToDetailDto(father, ownerMapForSingleId(father.getOwnerId()));
    var motherDto = mother == null ? null : mapper.entityToDetailDto(mother, ownerMapForSingleId(mother.getOwnerId()));
    validator.validateForUpdate(simpleHorse, fatherDto, motherDto);
    var updatedHorse = dao.update(horse);

    return mapper.entityToChildDetailDto(
        updatedHorse,
        ownerMapForMultipleId(
            Arrays.asList(updatedHorse.getOwnerId(), father == null ? null : father.getOwnerId(), mother == null ? null : mother.getOwnerId())),
        father,
        mother);
  }

  @Override
  public HorseChildDetailDto create(HorseCreateDto horse) throws ValidationException, ConflictException, NotFoundException {
    LOG.trace("create({})", horse);

    // get IDs of parents, fetch entity from db and convert to DTO for validator
    var father = horse.fatherId() == null ? null : dao.getById(horse.fatherId());
    var mother = horse.motherId() == null ? null : dao.getById(horse.motherId());
    var fatherDto = father == null ? null : mapper.entityToDetailDto(father, ownerMapForSingleId(father.getOwnerId()));
    var motherDto = mother == null ? null : mapper.entityToDetailDto(mother, ownerMapForSingleId(mother.getOwnerId()));
    validator.validateForInsert(horse, fatherDto, motherDto);

    var createdHorse = dao.create(horse);

    return mapper.entityToChildDetailDto(
        createdHorse,
        ownerMapForMultipleId(
            Arrays.asList(createdHorse.getOwnerId(), father == null ? null : father.getOwnerId(), mother == null ? null : mother.getOwnerId())),
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
        ownerMapForMultipleId(
            Arrays.asList(horse.getOwnerId(), father == null ? null : father.getOwnerId(), mother == null ? null : mother.getOwnerId())),
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

  private Map<Long, OwnerDto> ownerMapForMultipleId(Collection<Long> ownerIds) {
    ownerIds = ownerIds.stream().filter(id -> id != null).toList();
    try {
      return ownerIds == null
          ? null
          : ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("One of owners %d referenced by horse not found"
          .formatted(ownerIds.stream().map(id -> id.toString()).collect(Collectors.joining(","))));
    }
  }

}
