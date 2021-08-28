FROM openjdk:11-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

CMD ["--server.port=8080"]
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]