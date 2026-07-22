package in.techcamp.protospace.entity;
import lombok.Data;
@Data
public class PositionEntity {
  private Long id;
  private Long userId; // users テーブルの id
  private String name; // 役職名
}
