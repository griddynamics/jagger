FROM openjdk:8u102-jdk

ENV JWC_HTTP_PORT 80
ENV JWC_JDBC_DRIVER com.mysql.jdbc.Driver
ENV JWC_JDBC_URL jdbc:mysql://172.18.128.228:3306/jaggerdb
ENV JWC_JDBC_USER root
ENV JWC_JDBC_PASS root

COPY jagger-webclient.jar /com/griddynamics/jagger/jagger-webclient.jar
WORKDIR /com/griddynamics/jagger

EXPOSE ${JWC_HTTP_PORT}

CMD java -jar jagger-webclient.jar -httpPort=${JWC_HTTP_PORT} -Djdbc.driver=${JWC_JDBC_DRIVER} \
                                                            -Djdbc.url=${JWC_JDBC_URL} \
                                                            -Djdbc.user=${JWC_JDBC_USER} \
                                                            -Djdbc.password=${JWC_JDBC_PASS}
