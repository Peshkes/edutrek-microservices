FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY ./target/*.jar ./app.jar

EXPOSE 8093

CMD ["java", "-jar", "app.jar"]
