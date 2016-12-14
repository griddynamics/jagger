FROM openjdk:8

ENV JWC_HTTP_PORT 80
ENV JWC_JDBC_DRIVER com.mysql.jdbc.Driver
ENV JWC_JDBC_URL jdbc:mysql://localhost:3306/jaggerdb
ENV JWC_JDBC_USER root
ENV JWC_JDBC_PASS root
ENV MYSQL_WAIT 20
ENV JWC_HIBERNATE_DIALECT org.hibernate.dialect.MySQLDialect

ENV MYSQL_HOST mysql
ENV MYSQL_PORT 3306

# Set default timezone
ENV TZ=Europe/Moscow

# Get latest jagger-webclient
ADD ["http://nexus.vm.griddynamics.net:8081/nexus/service/local/artifact/maven/content?r=jagger-snapshots&g=com.griddynamics.jagger&a=webclient&c=war-exec&v=${project.version}", "/com/griddynamics/jagger/jagger-webclient.jar"]

ADD run.sh /com/griddynamics/jagger/run.sh
RUN chmod +x /com/griddynamics/jagger/run.sh

# Update and install nc:
RUN apt-get update && apt-get install -y netcat

WORKDIR /com/griddynamics/jagger

EXPOSE ${JWC_HTTP_PORT}

CMD ./run.sh
