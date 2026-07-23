package in.techcamp.protospace.controller;

import in.techcamp.protospace.service.PrototypeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) //  JWTなどのセキュリティフィルターを無効化（403回避）
public class PrototypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

        //  Controllerの引数 `Authentication authentication` に渡すためのダミー認証情報（ユーザーID: 1）を作成
        UsernamePasswordAuthenticationToken principal = 
                new UsernamePasswordAuthenticationToken("1", null);

        // MockMvcを使って、疑似的にPOSTリクエストを送信する
        // ※ Controllerの @PostMapping("/") に合わせるため末尾は "/"
        mockMvc.perform(multipart("/api/prototypes/")
                .file(imageFile) 
                .param("title", "テストタイトル") 
                .param("catchCopy", "テストキャッチコピー")
                .param("concept", "テストコンセプト")
                .principal(principal) //  ここでダミーの認証情報を直接リクエストに注入（500回避）
        )
                // 動作確認
                .andExpect(status().isOk()) 
                .andExpect(content().string("プロトタイプの投稿に成功しました。")); 

        verify(prototypeService).createPrototype(any(), eq(1L));
    }
}