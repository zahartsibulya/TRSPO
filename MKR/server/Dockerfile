FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app
COPY CollatzServer.java /app/
RUN javac CollatzServer.java

EXPOSE 9000
CMD ["java", "CollatzServer"]