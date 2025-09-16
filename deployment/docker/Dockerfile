# Imagen base con Java 21
FROM amazoncorretto:21-alpine-jdk

# Establecer zona horaria
ENV TZ=America/Bogota
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Crear directorio de la aplicación
WORKDIR /app

# Crear usuario y grupo no root
RUN addgroup -S -g 3000 appgroup && \
    adduser -S -u 1000 -G appgroup appuser && \
    mkdir -p /app/config /app/logs && \
    chown -R appuser:appgroup /app

# Copiar el JAR de la aplicación
COPY --chown=appuser:appgroup applications/app-service/build/libs/*.jar /app/application.jar

# Exponer puerto de la aplicación
EXPOSE 8080

# Configurar sistema de archivos de solo lectura para mayor seguridad
RUN chmod -R 555 /app && \
    chmod -R 755 /app/logs /app/config

# Cambiar al usuario no root
USER 1000:3000

# Establecer comando de inicio
ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", \
            "/app/application.jar"]
