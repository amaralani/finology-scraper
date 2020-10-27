FROM adoptopenjdk/openjdk11:jdk-11.0.8_10-alpine AS builder
ADD . /build
WORKDIR /build
RUN ./mvnw -B clean package -DskipTests=true

FROM adoptopenjdk/openjdk11:jdk-11.0.8_10-alpine
VOLUME /tmp
COPY ./target/finology-scraper-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:+CrashOnOutOfMemoryError -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]