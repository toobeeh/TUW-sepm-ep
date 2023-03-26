package at.ac.tuwien.sepm.assignment.individual.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.OwnerDao;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseServiceTest {
  @Autowired
  HorseService horseService;

  @Test
  public void getAllReturnsAllStoredHorses() {
    List<HorseDetailDto> horses = horseService.searchHorses(new HorseSearchDto(null, null, null, null, null, null))
        .toList();
    assertThat(horses.size()).isEqualTo(31);
    assertThat(horses)
        .map(HorseDetailDto::id, HorseDetailDto::sex)
        .contains(tuple(-31L, Sex.MALE));
  }

  @Test
  public void searchNameReturnsCorrectResults() {
    List<HorseDetailDto> horses = horseService.searchHorses(new HorseSearchDto("Charles", null, null, null, null, null))
        .toList();
    assertThat(horses.size()).isGreaterThan(0);
    assertThat(horses).extracting(HorseDetailDto::name).containsAnyElementsOf(List.of("Charles", "charles"));
  }

  @Test()
  public void newWithMaleMotherShouldError() {
    Assertions.assertThrowsExactly(ConflictException.class, () -> {
      var maleHorse = horseService.getById(-31L);
      var newHorse = new HorseCreateDto("Kek", "Hors", LocalDate.now(), Sex.MALE, null, null, maleHorse.withoutParents());

      assertThat(maleHorse.sex()).isEqualTo(Sex.MALE);
      horseService.create(newHorse);
    });
  }

  @Test
  public void sexShouldChange() throws NotFoundException, ValidationException, ConflictException {
    var hors = horseService.getById(-31L);
    var newHors =
        new HorseChildDetailDto(hors.id(), hors.name(), hors.description(), hors.dateOfBirth(), hors.sex() == Sex.MALE ? Sex.FEMALE : Sex.MALE, hors.owner(),
            hors.father(), hors.mother());
    var updatedHors = horseService.update(newHors);
    assertThat(updatedHors.sex()).isNotEqualTo(hors.sex());
  }
}
