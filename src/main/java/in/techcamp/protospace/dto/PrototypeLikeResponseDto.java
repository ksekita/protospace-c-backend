package in.techcamp.protospace.dto;

import lombok.Data;

@Data
public class PrototypeLikeResponseDto {
  private Long likeCount;
  private boolean isLiked;
}
