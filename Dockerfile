

FROM openjdk:21-jdk-slim


WORKDIR /app


COPY . /app


RUN apt-get update && apt-get install -y maven


RUN mvn clean package -DskipTests


EXPOSE 8081


CMD ["mvn", "spring-boot:run"]