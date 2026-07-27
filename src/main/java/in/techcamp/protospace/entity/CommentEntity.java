package in.techcamp.protospace.entity;

import lombok.Data;

@Data
public class CommentEntity {
  private Long id;
  private String content;
  private Long userId;
  private Long prototypeId;
}
