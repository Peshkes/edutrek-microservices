FROM eclipse-temurin:21-jre

WORKDIR /app

COPY ./target/*.jar ./app.jar

EXPOSE 8100

CMD ["java", "-jar", "app.jar"]
