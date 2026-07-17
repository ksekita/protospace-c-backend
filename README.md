# Protospace C (Backend)

このリポジトリは、Protospace C のバックエンドAPIサーバー（開発環境）です。
Docker (Dev Containers) を使用して、誰でも一瞬で同じ開発環境を構築できるように設計されています。

## 🛠️ 技術スタック

*   **Java:** 21 (Temurin)
*   **Framework:** Spring Boot 4.0.7
*   **Database:** PostgreSQL 16
*   **Migration:** Flyway
*   **O/R Mapper:** MyBatis (アノテーションスタイル / XML不使用)
*   **Security & Auth:** Spring Security, JJWT (0.12.7)
*   **Other Tools:** Lombok, Spring Boot DevTools

---

## 🚀 開発環境の立ち上げ手順

### 1. 前提条件の確認
お使いのPCに以下がインストールされていることを確認してください。
*   Docker Desktop (起動しておくこと)
*   Visual Studio Code
*   VS Code 拡張機能: **Dev Containers**

### 2. 共通ネットワークの作成 (初回のみ)
フロントエンドとバックエンドのコンテナ間で通信を行うため、ホストPC（WSL / Mac等）のターミナルで以下のコマンドを一度だけ実行してください。

```bash
docker network create shared-net
```

### 3. コンテナの起動
1. 本リポジトリを任意のディレクトリにクローンします。
2. VS Code でクローンしたフォルダを開きます。
3. 画面右下に **「Reopen in Container (コンテナで開き直す)」** というポップアップが出たらクリックします。
   * ポップアップが出ない場合は、`Ctrl + Shift + P` (Mac: `Cmd + Shift + P`) を押し、`Dev Containers: Reopen in Container` を選択します。
4. コンテナのビルドと起動が完了するまでしばらく待ちます（初回は数分かかります）。

### 4. アプリケーションの起動
VS Code内のターミナルを開き、以下のコマンドを実行します。

```bash
./gradlew bootRun
```

コード整形コマンド
```bash
././gradlew spotlessApply
```