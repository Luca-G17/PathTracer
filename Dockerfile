FROM amazoncorretto:20.0.0-alpine3.17

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve

COPY src ./src
COPY javafx-sdk-20.0.2 ./javafx
COPY target ./target
COPY DockerSetup.sh .
CMD ["/bin/sh", "-C", "./DockerSetup.sh"]
