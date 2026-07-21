package in.techcamp.protospace.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class UserEntity {
  private Long id;
  private String username;
  private String email;
  private String passwordHash;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // 1対多の情報を保持するためのリスト
  private List<String> positions;
  private List<String> jobs;
}