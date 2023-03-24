package at.ac.tuwien.sepm.assignment.individual.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    assertThat(horses.size()).isGreaterThanOrEqualTo(1); // TODO adapt to exact number of elements in test data later
    assertThat(horses)
        .map(HorseDetailDto::id, HorseDetailDto::sex)
        .contains(tuple(-1L, Sex.FEMALE));
  }
}
