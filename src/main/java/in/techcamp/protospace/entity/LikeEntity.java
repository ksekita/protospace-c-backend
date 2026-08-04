package in.techcamp.protospace.entity;

import lombok.Data;

@Data
public class LikeEntity {
  private Long id;
  private Long userId;
  private Long prototypeId;
}
