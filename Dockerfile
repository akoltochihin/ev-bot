FROM openjdk:17-jdk-slim
COPY . .
RUN ./mvnw clean package -DskipTests
EXPOSE 8080
ENTRYPOINT ["java","-jar","/target/evbot-1.0.0.jar"]
