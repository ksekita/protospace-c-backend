package in.techcamp.protospace.factory;

import in.techcamp.protospace.entity.PrototypeEntity;
import java.util.List;
import java.util.stream.IntStream;

public class PrototypeFactory {

  public static PrototypeEntity createDummyEntity(Long id) {
    PrototypeEntity entity = new PrototypeEntity();
    entity.setId(id);
    entity.setTitle("テストタイトル" + id);
    entity.setCatchCopy("キャッチコピー" + id);
    entity.setConcept("コンセプト" + id);
    entity.setImage("image" + id + ".png");
    entity.setUserId(id);

    return entity;
  }

  public static List<PrototypeEntity> createDummyList(int count) {
    return IntStream.rangeClosed(1, count).mapToObj(i -> createDummyEntity((long) i)).toList();
  }
}
