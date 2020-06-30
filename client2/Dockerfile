FROM maven:3.6.3-jdk-11 AS MAVEN_BUILD
LABEL stage=intermediate
COPY pom.xml /tmp/
RUN mvn -B dependency:go-offline -f /tmp/pom.xml
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package spring-boot:repackage

FROM adoptopenjdk/openjdk11:alpine-jre
EXPOSE 8280
RUN mkdir /app
COPY --from=MAVEN_BUILD /tmp/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]