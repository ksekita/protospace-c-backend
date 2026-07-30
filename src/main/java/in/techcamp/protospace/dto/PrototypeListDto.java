package in.techcamp.protospace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
// @AllArgsConstructor
public class PrototypeListDto {

  private Long id;
  private String title;
  private String catchCopy;
  private String image;
  private Long userId;
  private String name;
}
