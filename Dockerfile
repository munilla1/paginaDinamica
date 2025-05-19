# Usa la imagen de Java 21 (JDK)
FROM eclipse-temurin:21-jdk

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el JAR generado por Spring Boot
COPY target/*.jar app.jar

# Expone el puerto que Render usará (Render inyecta PORT como variable de entorno)
EXPOSE 8080

# Comando para ejecutar tu aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
