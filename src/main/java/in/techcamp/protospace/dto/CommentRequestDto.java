package in.techcamp.protospace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequestDto {
  @NotBlank(message = "コメントを入力してください")
  private String content;
}
