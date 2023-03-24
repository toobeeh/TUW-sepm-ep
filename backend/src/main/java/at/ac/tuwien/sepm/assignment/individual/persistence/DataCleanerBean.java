package at.ac.tuwien.sepm.assignment.individual.persistence;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

/**
 * This component is only created, if the profile {@code datagen} is active
 * You can activate this profile by adding {@code -Dspring.profiles.active=datagen} to your maven command line
 */
@Component
@Profile("!datagen")
public class DataCleanerBean {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final DataSource dataSource;

  /**
   * Executed once when the component is instantiated. Deletes possible test data.
   */
  public DataCleanerBean(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @PostConstruct
  public void generateData() throws SQLException {
    LOGGER.info("Removing possible test data…");
    try (var connection = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/removeTestData.sql"));
      LOGGER.info("Finished removing test data without error.");
    }
  }
}
