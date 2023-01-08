# 컨테이너1 - 빌더
FROM adoptopenjdk/openjdk8:latest as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# 컨테이너2
# COPY 명령어는 도커의 계층캐시 알고리즘을 사용하기 때문에 layer중에서 써드파티 라이브러리는 캐시 가능
FROM adoptopenjdk/openjdk8:latest
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]