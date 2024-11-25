FROM eclipse-temurin:22-jre

WORKDIR /app

COPY ./target/*.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
