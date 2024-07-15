#FROM appmon-app
FROM openjdk:17-jdk-alpine
WORKDIR /var/app
COPY target/*.jar /var/app/app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/var/app/app.jar"]