package in.techcamp.protospace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.exception.ResourceNotFoundException;
import in.techcamp.protospace.mapper.PrototypeMapper;
import in.techcamp.protospace.repository.PrototypeRepository;
import in.techcamp.protospace.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PrototypeServiceTest {

  @Mock private PrototypeRepository prototypeRepository;
  @Mock private UserRepository userRepository;
  @Mock private PrototypeMapper prototypeMapper;

  @InjectMocks private PrototypeService prototypeService;

  @Nested
  @DisplayName("プロトタイプ詳細取得処理 (getPrototypeDetail)")
  class GetPrototypeDetailTest {

    @Test
    @DisplayName("【正常系】存在するIDを指定した場合、詳細情報と投稿者名が返却されること")
    void getPrototypeDetail_Success() {
      PrototypeEntity prototype = new PrototypeEntity();
      prototype.setId(1L);
      prototype.setTitle("ProtoSpace");
      prototype.setCatchCopy("開発事例共有ツール");
      prototype.setConcept("コンセプト");
      prototype.setImage("test.png");
      prototype.setUserId(10L);

      UserEntity user = new UserEntity();
      user.setId(10L);
      user.setName("テスト太郎");

      when(prototypeRepository.findById(1L)).thenReturn(prototype);
      when(userRepository.selectById(10L)).thenReturn(user);

      PrototypeDetailResponseDto result = prototypeService.getPrototypeDetail(1L);

      assertThat(result.getId()).isEqualTo(1L);
      assertThat(result.getTitle()).isEqualTo("ProtoSpace");
      assertThat(result.getCatchCopy()).isEqualTo("開発事例共有ツール");
      assertThat(result.getUserId()).isEqualTo(10L);
      assertThat(result.getName()).isEqualTo("テスト太郎");
    }

    @Test
    @DisplayName("【異常系】存在しないIDを指定した場合、ResourceNotFoundExceptionが発生すること")
    void getPrototypeDetail_NotFound_ThrowsException() {
      when(prototypeRepository.findById(999L)).thenReturn(null);

      assertThatThrownBy(() -> prototypeService.getPrototypeDetail(999L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("プロトタイプが見つかりません");
    }
  }

  @Nested
  @DisplayName("プロトタイプ削除処理 (deletePrototype)")
  class DeletePrototypeTest {
    @Test
    @DisplayName("【正常系】自身のプロトタイプを指定した場合、削除メソッドが呼ばれること")
    void deletePrototype_Success() throws Exception {
      // 準備
      PrototypeEntity mockEntity = new PrototypeEntity();
      mockEntity.setId(1L);
      mockEntity.setUserId(10L); // 投稿者ID
      mockEntity.setImage("test-image.png");

      when(prototypeMapper.findById(1L)).thenReturn(mockEntity);

      // 実行
      prototypeService.deletePrototype(1L, 10L); // ユーザーID=10として実行

      // 検証 (Mapperのdeleteが正しく呼ばれたか)
      verify(prototypeMapper).delete(1L);
    }

    @Test
    @DisplayName("【異常系】存在しないプロトタイプを指定した場合、例外が発生すること")
    void deletePrototype_NotFound_ThrowsException() {
      // 準備: DBから見つからない状態
      when(prototypeMapper.findById(999L)).thenReturn(null);

      // 実行・検証
      assertThatThrownBy(() -> prototypeService.deletePrototype(999L, 10L))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("指定されたプロトタイプが見つかりません");

      // 削除処理が実行されていないことを確認
      verify(prototypeMapper, never()).delete(any());
    }

    @Test
    @DisplayName("【異常系】他人のプロトタイプを削除しようとした場合、例外が発生すること")
    void deletePrototype_Forbidden_ThrowsException() {
      // 準備
      PrototypeEntity mockEntity = new PrototypeEntity();
      mockEntity.setId(1L);
      mockEntity.setUserId(99L); // 投稿者IDは99

      when(prototypeMapper.findById(1L)).thenReturn(mockEntity);

      // 実行・検証 (ユーザーID=10として実行するとブロックされるはず)
      assertThatThrownBy(() -> prototypeService.deletePrototype(1L, 10L))
          .isInstanceOf(Exception.class)
          .hasMessage("他のユーザーの投稿を削除する権限がありません");

      // 削除処理が実行されていないことを確認
      verify(prototypeMapper, never()).delete(any());
    }
  }

  @Nested
  @DisplayName("特定ユーザーのプロトタイプ一覧取得処理 (getPrototypesByUserId)")
  class GetPrototypesByUserIdTest {

    @Test
    @DisplayName("【正常系】指定したユーザーIDのプロトタイプ一覧がDTOに変換されて返却されること")
    void getPrototypesByUserId_Success() {
      // 準備
      Long userId = 1L;

      PrototypeEntity entity1 = new PrototypeEntity();
      entity1.setId(10L);
      entity1.setTitle("タイトル1");
      entity1.setCatchCopy("キャッチコピー1");
      entity1.setImage("image1.png");
      entity1.setUserId(userId);

      PrototypeEntity entity2 = new PrototypeEntity();
      entity2.setId(11L);
      entity2.setTitle("タイトル2");
      entity2.setCatchCopy("キャッチコピー2");
      entity2.setImage("image2.png");
      entity2.setUserId(userId);

      when(prototypeMapper.findByUserId(userId)).thenReturn(java.util.List.of(entity1, entity2));

      // 実行
      java.util.List<in.techcamp.protospace.dto.UserPrototypeListDto> result =
          prototypeService.getPrototypesByUserId(userId);

      // 検証
      assertThat(result).hasSize(2);

      assertThat(result.get(0).getId()).isEqualTo(10L);
      assertThat(result.get(0).getName()).isEqualTo("テストユーザー");
      assertThat(result.get(0).getTitle()).isEqualTo("タイトル1");
      assertThat(result.get(0).getCatchCopy()).isEqualTo("キャッチコピー1");
      assertThat(result.get(0).getImage()).isEqualTo("image1.png");

      assertThat(result.get(1).getId()).isEqualTo(11L);
      assertThat(result.get(1).getTitle()).isEqualTo("タイトル2");
    }

    @Test
    @DisplayName("【正常系】投稿が0件の場合、空のリストが返却されること")
    void getPrototypesByUserId_Empty() {
      // 準備
      Long userId = 1L;
      when(prototypeMapper.findByUserId(userId)).thenReturn(java.util.Collections.emptyList());

      // 実行
      java.util.List<in.techcamp.protospace.dto.UserPrototypeListDto> result =
          prototypeService.getPrototypesByUserId(userId);

      // 検証
      assertThat(result).isEmpty();
    }
  }
}
