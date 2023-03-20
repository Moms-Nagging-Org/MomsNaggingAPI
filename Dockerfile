FROM openjdk:11

COPY build/libs/momsnagging.jar app.jar
#CMD ["java","-jar","/app.jar"]