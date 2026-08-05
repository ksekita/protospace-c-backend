package in.techcamp.protospace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrototypeLikeResponseDto {
  private Long likeCount;
  private boolean isLiked;
}
