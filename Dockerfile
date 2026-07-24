# ==========================================
# 1. ビルドステージ (アプリケーションのコンパイル)
# ==========================================
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Gradleラッパーと設定ファイルを先にコピー (キャッシュを活用して次回以降のビルドを高速化)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# gradlewに実行権限を付与
RUN chmod +x ./gradlew

# 依存関係をダウンロード
RUN ./gradlew dependencies --no-daemon || true

# ソースコードをコピーしてビルド (本番デプロイ用にテストはスキップ)
COPY src src
RUN ./gradlew build -x test --no-daemon

# ==========================================
# 2. 実行ステージ (軽量な実行環境の作成)
# ==========================================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# ビルドステージで作成されたjarファイルをコピー
COPY --from=builder /app/build/libs/*.jar app.jar

# 実行するユーザーをrootから一般ユーザーに変更 (セキュリティ対策として推奨)
RUN useradd -m appuser
USER appuser

# アプリケーションの起動
ENTRYPOINT ["java", "-jar", "app.jar"]