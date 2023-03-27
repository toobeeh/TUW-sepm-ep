package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class OwnerServiceTest {
  @Autowired
  OwnerService ownerService;

  @Test
  @DirtiesContext
  public void newOwnerShouldCreate() throws ValidationException, ConflictException, NotFoundException {
    var newOwner = new OwnerCreateDto("Service", "Owner", "Service@owner.com");
    var id = ownerService.create(newOwner).id();
    var check = ownerService.getById(id);

    assertThat(newOwner.firstName()).isEqualTo(check.firstName());
    assertThat(newOwner.lastName()).isEqualTo(check.lastName());
    assertThat(newOwner.email()).isEqualTo(check.email());
  }
}
