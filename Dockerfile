FROM openjdk:8-jdk-alpine
MAINTAINER ylin

ARG DEPENDENCY=target/dependency

VOLUME /app/test_app
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java", "-cp","app:app/lib/*","com.harrisburg.app.AppApplication"]
