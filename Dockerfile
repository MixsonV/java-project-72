FROM gradle:7.4.0-jdk17

WORKDIR /app

COPY gradle ./gradle
COPY gradlew .
COPY build.gradle.kts settings.gradle.kts ./

RUN gradle --no-daemon dependencies

COPY . .

RUN gradle --no-daemon shadowJar

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/app.jar .

EXPOSE 7071

CMD ["sh", "-c", "java -jar app.jar"]