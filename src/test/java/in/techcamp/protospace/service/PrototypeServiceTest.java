package in.techcamp.protospace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.dto.UserPrototypeListDto;
import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.exception.ResourceNotFoundException;
import in.techcamp.protospace.mapper.LikeMapper;
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
  @Mock private LikeMapper likeMapper;

  @InjectMocks private PrototypeService prototypeService;

  @Nested
  @DisplayName("プロトタイプ詳細取得処理 (getPrototypeDetail)")
  class GetPrototypeDetailTest {

    @Test
    @DisplayName("【正常系】存在するIDを指定した場合、詳細情報といいね情報が返却されること（ログイン時）")
    void getPrototypeDetail_Success_LoggedIn() {
      Long prototypeId = 1L;
      Long loggedInUserId = 10L;

      PrototypeEntity prototype = new PrototypeEntity();
      prototype.setId(prototypeId);
      prototype.setTitle("ProtoSpace");
      prototype.setCatchCopy("開発事例共有ツール");
      prototype.setConcept("コンセプト");
      prototype.setImage("test.png");
      prototype.setUserId(2L);

      UserEntity user = new UserEntity();
      user.setId(2L);
      user.setName("テスト太郎");

      when(prototypeRepository.findById(prototypeId)).thenReturn(prototype);
      when(userRepository.selectById(2L)).thenReturn(user);
      when(likeMapper.countByPrototypeId(prototypeId)).thenReturn(5L);
      when(likeMapper.existsLike(loggedInUserId, prototypeId)).thenReturn(true);

      PrototypeDetailResponseDto result = prototypeService.getPrototypeDetail(prototypeId, loggedInUserId);

      assertThat(result.getId()).isEqualTo(1L);
      assertThat(result.getTitle()).isEqualTo("ProtoSpace");
      assertThat(result.getName()).isEqualTo("テスト太郎");
      assertThat(result.getLikeCount()).isEqualTo(5L);
      assertTrue(result.isLiked());

      verify(likeMapper).countByPrototypeId(prototypeId);
      verify(likeMapper).existsLike(loggedInUserId, prototypeId);
    }

    @Test
    @DisplayName("【正常系】未ログインユーザー (0L) の場合、existsLikeが呼ばれずisLikedがfalseになること")
    void getPrototypeDetail_Success_Anonymous() {
      Long prototypeId = 1L;
      Long loggedInUserId = 0L;

      PrototypeEntity prototype = new PrototypeEntity();
      prototype.setId(prototypeId);
      prototype.setUserId(2L);

      when(prototypeRepository.findById(prototypeId)).thenReturn(prototype);
      when(userRepository.selectById(anyLong())).thenReturn(new UserEntity());
      when(likeMapper.countByPrototypeId(prototypeId)).thenReturn(10L);

      PrototypeDetailResponseDto result = prototypeService.getPrototypeDetail(prototypeId, loggedInUserId);

      assertThat(result.getLikeCount()).isEqualTo(10L);
      assertFalse(result.isLiked());

      verify(likeMapper).countByPrototypeId(prototypeId);
      verify(likeMapper, never()).existsLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("【異常系】存在しないIDを指定した場合、ResourceNotFoundExceptionが発生すること")
    void getPrototypeDetail_NotFound_ThrowsException() {
      when(prototypeRepository.findById(999L)).thenReturn(null);

      assertThatThrownBy(() -> prototypeService.getPrototypeDetail(999L, 0L))
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
      PrototypeEntity mockEntity = new PrototypeEntity();
      mockEntity.setId(1L);
      mockEntity.setUserId(10L);
      mockEntity.setImage("test-image.png");

      when(prototypeMapper.findById(1L)).thenReturn(mockEntity);

      prototypeService.deletePrototype(1L, 10L);

      verify(prototypeMapper).delete(1L);
    }

    @Test
    @DisplayName("【異常系】存在しないプロトタイプを指定した場合、例外が発生すること")
    void deletePrototype_NotFound_ThrowsException() {
      when(prototypeMapper.findById(999L)).thenReturn(null);

      assertThatThrownBy(() -> prototypeService.deletePrototype(999L, 10L))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("指定されたプロトタイプが見つかりません");

      verify(prototypeMapper, never()).delete(any());
    }

    @Test
    @DisplayName("【異常系】他人のプロトタイプを削除しようとした場合、例外が発生すること")
    void deletePrototype_Forbidden_ThrowsException() {
      PrototypeEntity mockEntity = new PrototypeEntity();
      mockEntity.setId(1L);
      mockEntity.setUserId(99L);

      when(prototypeMapper.findById(1L)).thenReturn(mockEntity);

      assertThatThrownBy(() -> prototypeService.deletePrototype(1L, 10L))
          .isInstanceOf(Exception.class)
          .hasMessage("他のユーザーの投稿を削除する権限がありません");

      verify(prototypeMapper, never()).delete(any());
    }
  }

  @Nested
  @DisplayName("特定ユーザーのプロトタイプ一覧取得処理 (getPrototypesByUserId)")
  class GetPrototypesByUserIdTest {

    @Test
    @DisplayName("【正常系】指定したユーザーIDのプロトタイプ一覧がDTOに変換されて返却されること")
    void getPrototypesByUserId_Success() {
      Long userId = 1L;
      Long loggedInUserId = 10L;

      UserPrototypeListDto dto1 = new UserPrototypeListDto();
      dto1.setId(10L);
      dto1.setName("テストユーザー");
      dto1.setTitle("タイトル1");
      dto1.setCatchCopy("キャッチコピー1");
      dto1.setImage("image1.png");

      UserPrototypeListDto dto2 = new UserPrototypeListDto();
      dto2.setId(11L);
      dto2.setName("テストユーザー");
      dto2.setTitle("タイトル2");
      dto2.setCatchCopy("キャッチコピー2");
      dto2.setImage("image2.png");

      when(prototypeMapper.findByUserId(userId, "DESC", loggedInUserId)).thenReturn(java.util.List.of(dto1, dto2));

      java.util.List<UserPrototypeListDto> result =
          prototypeService.getPrototypesByUserId(userId, "latest", loggedInUserId);

      assertThat(result).hasSize(2);
      assertThat(result.get(0).getId()).isEqualTo(10L);
      assertThat(result.get(0).getTitle()).isEqualTo("タイトル1");
    }

    @Test
    @DisplayName("【正常系】投稿が0件の場合、空のリストが返却されること")
    void getPrototypesByUserId_Empty() {
      Long userId = 1L;
      Long loggedInUserId = 10L;

      when(prototypeMapper.findByUserId(userId, "DESC", loggedInUserId)).thenReturn(java.util.Collections.emptyList());

      java.util.List<UserPrototypeListDto> result =
          prototypeService.getPrototypesByUserId(userId, "latest", loggedInUserId);

      assertThat(result).isEmpty();
    }
  }
}