# Paso 1: Compilar la aplicación
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# ESTA LÍNEA CORRIGE EL ERROR DE PERMISOS:
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Paso 2: Ejecutar la aplicación
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

