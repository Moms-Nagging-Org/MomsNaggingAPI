FROM openjdk:11
ADD build/libs/momsnagging.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]