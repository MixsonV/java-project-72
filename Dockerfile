FROM gradle:8.13-jdk21 AS build

WORKDIR /app

COPY app/gradle ./gradle
COPY app/gradlew .
COPY app/build.gradle.kts app/settings.gradle.kts ./

RUN ./gradle --no-daemon dependencies

COPY app/ .

RUN ./gradle --no-daemon shadowJar

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/app.jar .

EXPOSE 8080

CMD ["sh", "-c", "java -jar app.jar"]