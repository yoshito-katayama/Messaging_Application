# ベースイメージに OpenJDK を使用
FROM --platform=linux/amd64 openjdk:11-jdk-slim

# 作業ディレクトリを設定
WORKDIR /app

# 必要な JAR ファイルとソースコードをコピー
COPY ./com.ibm.mq.allclient-9.3.0.0.jar /app/
COPY ./javax.jms-api-2.0.1.jar /app/
COPY ./json-20220320.jar /app/
COPY ./db2jcc4.jar /app/
COPY ./app.java /app/

# Java ソースコードをコンパイル
RUN javac -cp ".:com.ibm.mq.allclient-9.3.0.0.jar:javax.jms-api-2.0.1.jar:json-20220320.jar:db2jcc4.jar" app.java

# 実行コマンドを設定
CMD ["java", "-cp", ".:com.ibm.mq.allclient-9.3.0.0.jar:javax.jms-api-2.0.1.jar:json-20220320.jar:db2jcc4.jar", "app"]
