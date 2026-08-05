package in.techcamp.protospace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserPrototypeListDto {
  private Long id;
  private String name;
  private String title;
  private String catchCopy;
  private String image;

  private Long likeCount; 
  @JsonProperty("isLiked")
  private boolean isLiked;
}
