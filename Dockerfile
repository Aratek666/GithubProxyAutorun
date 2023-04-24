FROM maven:3.8.4-openjdk-17 as BUILD

ADD ./pom.xml pom.xml
ADD ./src src/

RUN mvn clean package

From openjdk:17-oracle

COPY target/git-proxy-core-0.0.1-SNAPSHOT.jar git-proxy-core-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "git-proxy-core-0.0.1-SNAPSHOT.jar"]