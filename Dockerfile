# ---- AŞAMA 1: BUILD ----
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# JAR içinde migration dosyası var mı kontrol et
RUN jar tf target/*.jar | grep -i migration || echo "MIGRATION FILE NOT EXIST IN THE JAR FILE"

# ---- AŞAMA 2: RUN ----
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/fintrack-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]