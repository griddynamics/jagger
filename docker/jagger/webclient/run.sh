#!/usr/bin/env bash
# Wait for database to get available

#wait for mysql
sleep 30

java -jar jagger-webclient.jar -httpPort=${JWC_HTTP_PORT} -Djdbc.driver=${JWC_JDBC_DRIVER} -Djdbc.url=${JWC_JDBC_URL} -Djdbc.user=${JWC_JDBC_USER} -Djdbc.password=${JWC_JDBC_PASS} -Djdbc.hibernate.dialect=${JWC_HIBERNATE_DIALECT}
