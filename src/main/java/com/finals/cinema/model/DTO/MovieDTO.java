package com.finals.cinema.model.DTO;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class MovieDTO {

  private Integer id;
  private String title;
  private Integer ageRestriction;
  private String genre;
}
