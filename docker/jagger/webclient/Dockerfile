FROM openjdk:8u92-jdk-alpine

ENV JWC_HTTP_PORT 80
ENV JWC_JDBC_DRIVER com.mysql.jdbc.Driver
ENV JWC_JDBC_URL jdbc:mysql://localhost:3306/jaggerdb
ENV JWC_JDBC_USER root
ENV JWC_JDBC_PASS root

RUN apk --update add tzdata

# Set default timezone
ENV TZ=Europe/Moscow

# Get latest jagger-webclient
ADD ["http://nexus.vm.griddynamics.net:8081/nexus/service/local/artifact/maven/content?r=jagger-snapshots&g=com.griddynamics.jagger&a=webclient&c=exec-war&v=${project.version}", "/com/griddynamics/jagger/jagger-webclient.jar"] 

WORKDIR /com/griddynamics/jagger

EXPOSE ${JWC_HTTP_PORT}

# Command "sleep 10" needed for initialisation of database. Watch JFG-939.
CMD sleep 10 && java -jar jagger-webclient.jar -httpPort=${JWC_HTTP_PORT} -Djdbc.driver=${JWC_JDBC_DRIVER} \
                                                            -Djdbc.url=${JWC_JDBC_URL} \
                                                            -Djdbc.user=${JWC_JDBC_USER} \
                                                            -Djdbc.password=${JWC_JDBC_PASS}
