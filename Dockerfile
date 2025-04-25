FROM eclipse-temurin:21-jdk-alpine-3.22
ENV PORT=8081
EXPOSE 8081
COPY /target/realtime-chatapp-user-service-0.0.1-SNAPSHOT.jar realtime-chatapp-user-service.jar
ENTRYPOINT ["java", "-jar", "realtime-chatapp-user-service.jar"]