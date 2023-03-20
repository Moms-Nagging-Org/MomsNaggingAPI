FROM openjdk:17

COPY build/libs/momsnagging.jar app.jar
#CMD ["java","-jar","/app.jar"]