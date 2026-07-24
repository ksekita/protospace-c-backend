FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# キャッシュを有効活用するため、設定ファイルとラッパーを先にコピー
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Git側でgradlewに実行権限をつけていれば、chmodは不要
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

# ソースコードをコピーしてビルド
COPY src src
RUN ./gradlew build -x test --no-daemon

# -plain.jar 以外を app.jar として特定・コピー
RUN find build/libs -name "*.jar" -not -name "*-plain.jar" -exec cp {} app.jar \;

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

ENV TZ=Asia/Tokyo

# ホームディレクトリを持たないシステムユーザーとして作成
RUN useradd -r -s /bin/false appuser

# COPY時に直接オーナーを変更してイメージサイズを削減
COPY --chown=appuser:appuser --from=builder /app/app.jar app.jar

USER appuser

EXPOSE 8080

# exec を追加して PID 1 問題を回避しつつ JAVA_OPTS を展開
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]