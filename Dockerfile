FROM gradle:jdk21-alpine as builder
LABEL author = "zhitkevich"
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM eclipse-temurin:17-jre-alpine
RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar

ENTRYPOINT ["java","-jar","/app/*.jar"]
