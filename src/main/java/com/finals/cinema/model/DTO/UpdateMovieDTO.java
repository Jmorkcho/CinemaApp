package com.finals.cinema.model.DTO;

import com.finals.cinema.model.entity.Genre;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class UpdateMovieDTO {

  private Integer id;

  private String title;

  @Positive(message = "Age restriction cannot be less than 1")
  private Integer ageRestriction;

  private Genre genre;
}
