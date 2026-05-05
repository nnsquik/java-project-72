FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY app/ ./app/

WORKDIR /app/app

RUN ./gradlew shadowJar

CMD ["java", "-jar", "build/libs/app.jar"]
