# 빌드 단계
FROM ubuntu:latest AS build

# 필요한 패키지를 설치합니다.
RUN apt-get update && apt-get install -y openjdk-17-jdk wget unzip

# Gradle 설치
RUN wget https://services.gradle.org/distributions/gradle-7.6.1-bin.zip -P /tmp \
    && unzip -d /opt/gradle /tmp/gradle-7.6.1-bin.zip \
    && ln -s /opt/gradle/gradle-7.6.1 /opt/gradle/latest

# Gradle 환경 변수 설정
ENV GRADLE_HOME=/opt/gradle/latest
ENV PATH=${GRADLE_HOME}/bin:${PATH}

# 작업 디렉토리를 설정합니다.
WORKDIR /usr/src/app

# 프로젝트 파일들을 복사합니다.
COPY . .

# Gradle을 사용하여 애플리케이션을 빌드합니다.
RUN gradle build

# 실행 단계
FROM ubuntu:latest

# Java 17을 설치합니다.
RUN apt-get update && apt-get install -y openjdk-17-jdk

# 빌드된 JAR 파일을 복사합니다.
COPY --from=build /usr/src/app/build/libs/java-was-1.0-SNAPSHOT.jar /app/myapp.jar

# 컨테이너의 8080 포트를 노출합니다.
EXPOSE 8080

# 애플리케이션을 실행합니다.
CMD ["java", "-jar", "/app/myapp.jar"]
