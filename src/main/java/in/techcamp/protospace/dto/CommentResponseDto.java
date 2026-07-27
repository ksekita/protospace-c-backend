package in.techcamp.protospace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
  private Long id;
  private String content;
  private Long userId;
  private Long prototypeId;
  private String userName;
}
