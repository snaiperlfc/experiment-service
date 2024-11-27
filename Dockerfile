FROM bellsoft/liberica-openjre-alpine:17.0.11
VOLUME /tmp
ADD target/*.jar app.jar
ENTRYPOINT exec java -jar /app.jar
