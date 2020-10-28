FROM adoptopenjdk/openjdk11:jdk-11.0.8_10-alpine
VOLUME /tmp
COPY ./target/finology-scraper-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]