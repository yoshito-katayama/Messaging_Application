# ベースイメージとしてOpenJDKを使用
FROM openjdk:17-slim

# アプリケーションのjarファイルをコンテナにコピー
ARG JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# アプリケーションを起動するコマンド
ENTRYPOINT ["java","-jar","/app.jar"]