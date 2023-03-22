package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseChildDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.lang.invoke.MethodHandles;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = ?"
      + "  , description = ?"
      + "  , date_of_birth = ?"
      + "  , sex = ?"
      + "  , owner_id = ?"
      + "  , father_id = ?"
      + "  , mother_id = ?"
      + " WHERE id = ?";
  private static final String SQL_CREATE = "INSERT INTO " + TABLE_NAME
      + "(name, description, date_of_birth, sex, owner_id, father_id, mother_id) " +
      "VALUES(?, ? ,?, ?, ?, ?, ?)";

  private final JdbcTemplate jdbcTemplate;

  public HorseJdbcDao(
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Horse> searchAll(HorseSearchDto searchFilter) {
    LOG.trace("searchAll()");
    List<Horse> horses;

    Function<String, String> like = str -> "%" + str + "%";

    var sqlParams = new MapSqlParameterSource();
    var namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    String sql = "SELECT * FROM " + TABLE_NAME + " WHERE 1=1";

    if (searchFilter.name() != null) {
      sql += " AND name LIKE :name";
      sqlParams.addValue("name", like.apply(searchFilter.name()));
    }
    if (searchFilter.description() != null) {
      sql += " AND description LIKE :description";
      sqlParams.addValue("description", like.apply(searchFilter.description()));
    }
    if (searchFilter.sex() != null) {
      sql += " AND sex = :sex";
      sqlParams.addValue("sex", searchFilter.sex());
    }
    if (searchFilter.bornBefore() != null) {
      sql += " AND date_of_birth <  = :birth";
      sqlParams.addValue("birth", Date.valueOf(searchFilter.bornBefore()));
    }
    if (searchFilter.ownerName() != null) {
      sql += " AND owner_id IN (SELECT * FROM owners WHERE first_name LIKE :owner OR last_name LIKE :owner";
      sqlParams.addValue("owner", like.apply(searchFilter.ownerName()));
    }
    if (searchFilter.limit() != null) {
      sql += " LIMIT :limit";
      sqlParams.addValue("limit", searchFilter.limit());
    }

    horses = namedTemplate.query(
        sql,
        sqlParams,
        this::mapRow
    );

    return horses;
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.get(0);
  }

  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    int updated = jdbcTemplate.update(SQL_DELETE, id);

    if (updated == 0) {
      throw new NotFoundException("Could not delete horse with ID " + id + ", because it does not exist");
    }
  }


  @Override
  public Horse update(HorseChildDetailDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);
    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex().toString(),
        horse.ownerId(),
        horse.fatherId(),
        horse.motherId(),
        horse.id());
    if (updated == 0) {
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }

    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setDescription(horse.description())
        .setDateOfBirth(horse.dateOfBirth())
        .setSex(horse.sex())
        .setOwnerId(horse.ownerId())
        ;
  }


  @Override
  public Horse create(HorseCreateDto horse) {
    LOG.trace("create({})", horse);

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement stmt = con.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, horse.name());
      stmt.setString(2, horse.description());
      stmt.setDate(3, Date.valueOf(horse.dateOfBirth()));
      stmt.setString(4, horse.sex().name());
      stmt.setObject(5, horse.ownerId());
      stmt.setObject(6, horse.fatherId());
      stmt.setObject(7, horse.motherId());

      return stmt;
    }, keyHolder);

    Number key = keyHolder.getKey();
    if (key == null) {
      // This should never happen. If it does, something is wrong with the DB or the way the prepared statement is set up.
      throw new FatalException("Could not extract key for newly created owner. There is probably a programming error… kek");
    }

    return new Horse()
        .setId(key.longValue())
        .setName(horse.name())
        .setDescription(horse.description())
        .setDateOfBirth(horse.dateOfBirth())
        .setSex(horse.sex())
        .setOwnerId(horse.ownerId())
        ;
  }


  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setDescription(result.getString("description"))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setSex(Sex.valueOf(result.getString("sex")))
        .setOwnerId(result.getObject("owner_id", Long.class))
        .setFatherId(result.getObject("father_id", Long.class))
        .setMotherId(result.getObject("mother_id", Long.class))
        ;
  }
}
