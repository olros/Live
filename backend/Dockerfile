FROM openjdk:11
VOLUME /tmp
ADD build/libs/live-0.0.1-SNAPSHOT.jar live.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/live.jar"]