# Etapa 1: Use uma imagem base do Java 
FROM eclipse-temurin:21-jdk-jammy

# Define um argumento para o nome do arquivo JAR
ARG JAR_FILE=target/*.jar

# Define o diretório de trabalho dentro do container
WORKDIR /opt/app

# Copia o arquivo JAR construído pelo Maven para dentro do container
COPY ${JAR_FILE} app.jar

# Expõe a porta 8080, que é a porta em que a aplicação Spring roda
# Isso informa ao Docker que o container escuta nesta porta
EXPOSE 8080

# Comando para iniciar a aplicação quando o container for executado
ENTRYPOINT ["java","-jar","app.jar"]