package in.techcamp.protospace.factory;

import in.techcamp.protospace.dto.PrototypeListDto;
import java.util.List;
import java.util.stream.IntStream;

public class PrototypeFactory {

  public static PrototypeListDto createDummyEntity(Long id) {
    PrototypeListDto dto = new PrototypeListDto();
    dto.setId(id);
    dto.setTitle("テストタイトル" + id);
    dto.setCatchCopy("キャッチコピー" + id);
    dto.setImage("image" + id + ".png");
    dto.setUserId(id);
    dto.setName("name" + id);


    return dto;
  }

  public static List<PrototypeListDto> createDummyList(int count) {
    return IntStream.rangeClosed(1, count).mapToObj(i -> createDummyEntity((long) i)).toList();
  }
}
