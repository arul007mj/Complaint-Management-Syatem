FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/complaint_management-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]

