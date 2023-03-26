package at.ac.tuwien.sepm.assignment.individual.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class HorseEndpointTest {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  @Test
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<HorseDetailDto> horseResult = objectMapper.readerFor(HorseDetailDto.class).<HorseDetailDto>readValues(body).readAll();

    assertThat(horseResult).isNotNull();
    assertThat(horseResult.size()).isEqualTo(31);
    assertThat(horseResult)
        .extracting(HorseDetailDto::id, HorseDetailDto::name)
        .contains(tuple(-31L, "Charles II"));
  }

  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }

  @Test
  public void searchNameReturnsCorrectResults() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses?name=Charles")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<HorseDetailDto> horses = objectMapper.readerFor(HorseDetailDto.class).<HorseDetailDto>readValues(body).readAll();
    assertThat(horses.size()).isGreaterThan(0);
    assertThat(horses).extracting(HorseDetailDto::name).containsAnyElementsOf(List.of("Charles", "charles"));
  }

  @Test()
  public void newWithMaleMotherShouldError() throws Exception {
    byte[] getBody = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses/-31")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();
    HorseChildDetailDto maleHorse = objectMapper.readValue(getBody, HorseChildDetailDto.class);

    var newHorse = new HorseCreateDto("Kek", "Hors", LocalDate.now(), Sex.MALE, null, null, maleHorse.withoutParents());

    mockMvc
        .perform(MockMvcRequestBuilders
            .post("/horses")
            .content(objectMapper.writeValueAsString(newHorse))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());
  }

  @Test
  @DirtiesContext
  public void sexShouldChange() throws Exception {
    byte[] getBody = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses/-31")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();
    HorseChildDetailDto maleHorse = objectMapper.readValue(getBody, HorseChildDetailDto.class);

    var newHors = new HorseChildDetailDto(maleHorse.id(), maleHorse.name(), maleHorse.description(), maleHorse.dateOfBirth(),
        maleHorse.sex() == Sex.MALE ? Sex.FEMALE : Sex.MALE, maleHorse.owner(),
        maleHorse.father(), maleHorse.mother());

    byte[] putBody = mockMvc
        .perform(MockMvcRequestBuilders
            .put("/horses/-31")
            .content(objectMapper.writeValueAsString(newHors))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    HorseChildDetailDto femaleHorse = objectMapper.readValue(putBody, HorseChildDetailDto.class);
    assertThat(femaleHorse.sex()).isEqualTo(Sex.FEMALE);
  }
}
