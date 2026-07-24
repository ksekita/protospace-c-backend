package in.techcamp.protospace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import in.techcamp.protospace.dto.PrototypeDetailResponseDto;
import in.techcamp.protospace.entity.PrototypeEntity;
import in.techcamp.protospace.entity.UserEntity;
import in.techcamp.protospace.exception.ResourceNotFoundException;
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
      user.setUsername("テスト太郎");

      when(prototypeRepository.findById(1L)).thenReturn(prototype);
      when(userRepository.selectById(10L)).thenReturn(user);

      PrototypeDetailResponseDto result = prototypeService.getPrototypeDetail(1L);

      assertThat(result.getId()).isEqualTo(1L);
      assertThat(result.getTitle()).isEqualTo("ProtoSpace");
      assertThat(result.getCatchCopy()).isEqualTo("開発事例共有ツール");
      assertThat(result.getUserId()).isEqualTo(10L);
      assertThat(result.getUserName()).isEqualTo("テスト太郎");
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
}