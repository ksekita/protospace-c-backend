package in.techcamp.protospace.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

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

  private Long likeCount; 
  @JsonProperty("isLiked")
  private boolean isLiked;
}
