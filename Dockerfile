FROM maven:3.8.6-openjdk-11

USER root
RUN whoami
RUN groupadd -g 1000 AB_DOCKER_SETUP_GROUP
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN mvn dependency:resolve

COPY src ./src
COPY data ./data
COPY libs ./

EXPOSE 8080
CMD ["mvn", "spring-boot:run"]