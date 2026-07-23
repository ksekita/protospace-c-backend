package in.techcamp.protospace.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;


import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
  private final SecretKey key;
  private final long expiration;

  //設定値の読み込みと鍵の作成
  public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                          @Value("${app.jwt.expiration}") long expiration) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expiration = expiration;
  }

  //JWTトークンの生成
  public String generateToken(String userId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
        .subject(userId)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(key)
        .compact();
  }

  //秘密鍵を使ってトークンからメールアドレスを取り出す
  public String getUserIdFromToken(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  //トークンの有効性チェック
  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }
}