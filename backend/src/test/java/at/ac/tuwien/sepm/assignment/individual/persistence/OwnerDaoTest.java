package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class OwnerDaoTest {

  @Autowired
  OwnerDao ownerDao;

  @Test()
  @DirtiesContext()
  public void newOwnerShouldCreate() throws ValidationException, ConflictException, NotFoundException {
    var newOwner = new OwnerCreateDto("Kek", "Owner", "kek@owner.com");
    var id = ownerDao.create(newOwner).getId();
    var check = ownerDao.getById(id);

    assertThat(newOwner.firstName()).isEqualTo(check.getFirstName());
    assertThat(newOwner.lastName()).isEqualTo(check.getLastName());
    assertThat(newOwner.email()).isEqualTo(check.getEmail());
  }
}
