
# Etapa 1: compilar CREINFOR con Maven y Java 17
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests clean package

# Etapa 2: ejecutar CREINFOR con Tomcat 9
FROM tomcat:9.0-jdk17-temurin

# Desplegar la aplicación en la raíz
RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY --from=build /app/target/*.war \
    /usr/local/tomcat/webapps/ROOT.war

# Railway asigna el puerto 8080
ENV PORT=8080

EXPOSE 8080

CMD ["catalina.sh", "run"]

