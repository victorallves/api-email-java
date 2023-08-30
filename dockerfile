FROM maven:3.8.4-openjdk-11 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src/ ./src/

RUN mvn package

FROM tomcat:9.0.54-jdk11-openjdk-slim

COPY --from=build /app/target/app-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/app.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
