package at.ac.tuwien.sepm.assignment.individual.mapper;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Owner;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OwnerMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public OwnerDto entityToDto(Owner owner) {
    LOG.trace("entityToDto({})", owner);
    if (owner == null) {
      return null;
    }
    return new OwnerDto(
        owner.getId(),
        owner.getFirstName(),
        owner.getLastName(),
        owner.getEmail());
  }
}
