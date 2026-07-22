package in.techcamp.protospace.controller;

import in.techcamp.protospace.mapper.PrototypeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// ★ここを最新のパッケージに修正！
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.mock.web.MockMultipartFile;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
public class PrototypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean 
    private PrototypeMapper prototypeMapper;
    
    @Test
    @WithMockUser
    public void testCreatePrototype() throws Exception {
        // ==========================================
        // 1. ダミーの画像ファイルを作成する
        // ==========================================
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",                 
                "test-image.png",         // 仮のファイル名
                "image/png",              // ファイルの形式
                "dummy image data".getBytes() // 仮のデータ
        );

        // ==========================================
        // 2. テスト用のJWT（トークン）を生成する
        // ==========================================
        // Controller側で使っている秘密鍵と全く同じものを使います
        String secretKeyString = "my-super-secret-key-for-protospace-which-is-very-long";
        SecretKey key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        
        // 0.12.7の書き方で、ユーザーID「1」を持つトークンを新しく作ります
        String testToken = Jwts.builder()
                .subject("1")
                .signWith(key)
                .compact();

        // ==========================================
        // 3. MockMvcを使って、疑似的にPOSTリクエストを送信！
        // ==========================================
        mockMvc.perform(multipart("/api/prototypes/")
                .file(imageFile) // 画像をセット
                .param("title", "テストタイトル") // テキストをセット
                .param("catchCopy", "テストキャッチコピー")
                .param("concept", "テストコンセプト")
                .header("Authorization", "Bearer " + testToken) // JWTをセット
                .with(csrf())
    )
    
                // 4. 結果の検証（答え合わせ）
                .andExpect(status().isOk()) // HTTPステータスが 200 OK であること
                .andExpect(content().string("プロトタイプの投稿に成功しました！")); // 返ってくる文字が一致すること

        verify(prototypeMapper).insert(any());
    }
}