#starts with the base image containing javaruntime
FROM openjdk:17-jdk-slim
#informations who maintains it
MAINTAINER prashantLabs

#add the application's jar to image
COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

#execute the application
ENTRYPOINT ["java","-jar","accounts-0.0.1-SNAPSHOT.jar"]