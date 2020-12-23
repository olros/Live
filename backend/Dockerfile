FROM openjdk:11
MAINTAINER olafros.com
VOLUME /tmp
EXPOSE 8080
ADD build/libs/live-0.0.1-SNAPSHOT.jar live.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/live.jar"]