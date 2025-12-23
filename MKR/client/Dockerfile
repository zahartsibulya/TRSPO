FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app
COPY CollatzClient.java /app/
RUN javac CollatzClient.java

CMD ["java", "CollatzClient"]