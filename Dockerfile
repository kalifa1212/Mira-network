
# Étape 1 : Build Maven
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

COPY . .

# Compiler le projet depuis le POM parent
RUN mvn clean package -DskipTests

# Vérifier les JAR générés
RUN find /app -type f -name "*.jar"

# Étape 2 : Image finale
FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

# Copier le JAR du module mira-starter
COPY --from=build /app/mira-starter/target/mira-starter-1.0.jar mira.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "mira.jar"]


#FROM maven:3.8.5-openjdk-17 As build
#COPY . .
#RUN mvn clean package -DskipeTests
#
#FROM openjdk:17.0.1-jdk-slim
#COPY --from=build /target/ModuleUser-0.0.1-SNAPSHOT.jar ModuleUser.jar
#EXPOSE 8080
#ENTRYPOINT [ "java","-jar","ModuleUser.jar" ]