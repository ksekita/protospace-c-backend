package in.techcamp.protospace.entity;

import lombok.Data;

@Data
public class UserEntity {
  private Long id;
  private String username;
  private String email;
  private String passwordHash;
}
