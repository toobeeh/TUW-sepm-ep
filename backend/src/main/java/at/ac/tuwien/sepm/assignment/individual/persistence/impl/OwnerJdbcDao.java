package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Owner;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.OwnerDao;
import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OwnerJdbcDao implements OwnerDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "owner";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME + " WHERE id IN (:ids)";
  private static final String SQL_SELECT_SEARCH = "SELECT * FROM " + TABLE_NAME
      + " WHERE UPPER(first_name||' '||last_name) like UPPER('%'||COALESCE(?, '')||'%')";
  private static final String SQL_SELECT_SEARCH_LIMIT_CLAUSE = " LIMIT ?";
  private static final String SQL_CREATE = "INSERT INTO " + TABLE_NAME + " (first_name, last_name, email) VALUES (?, ?, ?)";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public OwnerJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }


  @Override
  public Owner getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Owner> owners = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);
    if (owners.isEmpty()) {
      throw new NotFoundException("Owner with ID %d not found".formatted(id));
    }
    if (owners.size() > 1) {
      // If this happens, something is wrong with either the DB or the select
      throw new FatalException("Found more than one owner with ID %d".formatted(id));
    }
    return owners.get(0);
  }

  @Override
  public Owner create(OwnerCreateDto newOwner) {
    LOG.trace("create({})", newOwner);

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement stmt = con.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, newOwner.firstName());
      stmt.setString(2, newOwner.lastName());
      stmt.setString(3, newOwner.email());
      return stmt;
    }, keyHolder);

    Number key = keyHolder.getKey();
    if (key == null) {
      // This should never happen. If it does, something is wrong with the DB or the way the prepared statement is set up.
      throw new FatalException("Could not extract key for newly created owner. There is probably a programming error…");
    }

    return new Owner()
        .setId(key.longValue())
        .setFirstName(newOwner.firstName())
        .setLastName(newOwner.lastName())
        .setEmail(newOwner.email())
        ;
  }

  @Override
  public Collection<Owner> getAllById(Collection<Long> ids) {
    LOG.trace("getAllById({})", ids);
    var statementParams = Collections.singletonMap("ids", ids);
    return jdbcNamed.query(SQL_SELECT_ALL, statementParams, this::mapRow);
  }

  @Override
  public Collection<Owner> search(OwnerSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var query = SQL_SELECT_SEARCH;
    var params = new ArrayList<>();
    params.add(searchParameters.name());
    var maxAmount = searchParameters.maxAmount();
    if (maxAmount != null) {
      query += SQL_SELECT_SEARCH_LIMIT_CLAUSE;
      params.add(maxAmount);
    }
    return jdbcTemplate.query(query, this::mapRow, params.toArray());
  }

  private Owner mapRow(ResultSet resultSet, int i) throws SQLException {
    return new Owner()
        .setId(resultSet.getLong("id"))
        .setFirstName(resultSet.getString("first_name"))
        .setLastName(resultSet.getString("last_name"))
        .setEmail(resultSet.getString("email"))
        ;
  }
}
