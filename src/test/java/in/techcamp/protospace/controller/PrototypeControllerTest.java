package in.techcamp.protospace.controller;

import in.techcamp.protospace.service.PrototypeService;
import in.techcamp.protospace.security.JwtTokenProvider; // 🌟 追加

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc 
public class PrototypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider; 

    @MockitoBean 
    private PrototypeService prototypeService;
    
    @Test
    public void testCreatePrototype() throws Exception {
        // ダミーデータの作成
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",                  
                "test-image.png",         
                "image/png",              
                "dummy image data".getBytes() 
        );

        // テスト用の「本物のJWTトークン」を生成（ユーザーID: "1" をセット）
        String token = jwtTokenProvider.generateToken("1");

        // MockMvcを使って、疑似的にPOSTリクエストを送信する
        mockMvc.perform(multipart("/api/prototypes/")
                .file(imageFile) 
                .param("title", "テストタイトル") 
                .param("catchCopy", "テストキャッチコピー")
                .param("concept", "テストコンセプト")
                .header("Authorization", "Bearer " + token) 
        )
                // 動作確認
                .andExpect(status().isOk()) 
                .andExpect(content().string("プロトタイプの投稿に成功しました。")); 

        verify(prototypeService).createPrototype(any(), eq(1L));
    }
}