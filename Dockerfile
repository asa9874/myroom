FROM openjdk:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test -x spotlessCheck -x spotlessApply

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
