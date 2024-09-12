# Etapa de construção
FROM maven:3.8.3-openjdk-17 AS build

# Defina o diretório de trabalho
WORKDIR /app

# Copie o arquivo pom.xml e baixe as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie o restante do código fonte
COPY . .

# Compile o projeto e crie o JAR
RUN mvn clean package -DskipTests

# Etapa de execução
FROM openjdk:17-jdk

# Adicione o JAR da aplicação ao contêiner
COPY --from=build /app/target/*.jar /app/app.jar

# Defina o diretório de trabalho
WORKDIR /app

# Exponha a porta que a aplicação usará
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]