package at.ac.tuwien.sepm.assignment.individual.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;

import java.time.LocalDate;
import java.util.List;

import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseDaoTest {

  @Autowired
  HorseDao horseDao;

  @Test
  public void getAllReturnsAllStoredHorses() {
    List<Horse> horses = horseDao.searchAll(new HorseSearchDto(null, null, null, null, null, null));
    assertThat(horses.size()).isEqualTo(31);
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName)
        .contains(tuple(-31L, "Charles II"));
  }

  @Test
  public void searchNameReturnsCorrectResults() {
    List<Horse> horses = horseDao.searchAll(new HorseSearchDto("Charles", null, null, null, null, null));
    assertThat(horses.size()).isGreaterThan(0);
    assertThat(horses).extracting(Horse::getName).containsAnyElementsOf(List.of("Charles", "charles"));
  }

  @Test
  public void getForNonExistentHorseShouldError() {
    Assertions.assertThrowsExactly(NotFoundException.class, () -> {
      horseDao.getById(100);
    });
  }

  @Test
  public void ancestorsAreLimited() throws NotFoundException {
    List<Horse> horsesLimited = horseDao.getAncestors(-31, 5);
    List<Horse> horsesUnLimited = horseDao.getAncestors(-31, 10);

    assertThat(horsesLimited.size()).isLessThan(horsesUnLimited.size());
    assertThat(horsesLimited).extracting(Horse::getName).doesNotContainAnyElementsOf(List.of("Maximilian I"));
    assertThat(horsesUnLimited).extracting(Horse::getName).containsAnyElementsOf(List.of("Maximilian I"));
  }

  @Test
  @DirtiesContext
  public void sexShouldChange() throws NotFoundException {
    var hors = horseDao.getById(-31L);
    var newHors =
        new HorseChildDetailDto(hors.getId(), hors.getName(), hors.getDescription(), hors.getDateOfBirth(), hors.getSex() == Sex.MALE ? Sex.FEMALE : Sex.MALE,
            null,
            null, null);
    var updatedHors = horseDao.update(newHors);
    assertThat(updatedHors.getSex()).isNotEqualTo(hors.getSex());
  }
}
