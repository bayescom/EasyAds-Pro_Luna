# build 
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
COPY . /Luna/

WORKDIR /Luna
ARG BUILD_PROFILE=prod
RUN mvn clean package -P${BUILD_PROFILE}

# deploy
FROM tomcat:11-jdk21-temurin-jammy
WORKDIR /usr/local/tomcat

COPY --from=build /Luna/target/Luna.war ./webapps

# env
ENV MYSQL_URL=127.0.0.1
ENV MYSQL_PORT=3306
ENV MYSQL_USER=user
ENV MYSQL_PASSWORD=password
ENV REDIS_URL=127.0.0.1
ENV REDIS_PORT=6379
ENV REDIS_PASSWORD=password

EXPOSE 8080
