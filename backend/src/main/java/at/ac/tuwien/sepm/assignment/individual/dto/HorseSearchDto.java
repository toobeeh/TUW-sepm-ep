package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO to bundle the query parameters used in searching horses.
 * Each field can be null, in which case this field is not filtered by.
 */
public record HorseSearchDto(
    String name,
    String description,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate bornBefore,
    Sex sex,
    String ownerName,
    Integer limit
) {
}
