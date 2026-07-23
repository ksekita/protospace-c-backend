package in.techcamp.protospace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrototypeDetailResponseDto {

  private Long id;
  private String title;
  private String catchCopy;
  private String concept;
  private String image;
  private Long userId;
  private String userName; 
}