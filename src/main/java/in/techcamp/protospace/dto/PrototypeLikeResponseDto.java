package in.techcamp.protospace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
public class PrototypeLikeResponseDto {
  private Long likeCount;

  @JsonProperty("isLiked")//@Dataの仕様により、isが消えてしまうため、JsonPropertyを追加。
  private boolean isLiked;
}
