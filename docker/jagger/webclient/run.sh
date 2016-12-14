#!/usr/bin/env bash
# Wait for database to get available

while ! nc $MYSQL_HOST $MYSQL_PORT >/dev/null 2>&1 < /dev/null; do
  echo "$(date) - waiting for ${MYSQL_HOST}:${MYSQL_PORT}..."
  sleep 1
done


java -jar jagger-webclient.jar -httpPort=${JWC_HTTP_PORT} -Djdbc.driver=${JWC_JDBC_DRIVER} -Djdbc.url=${JWC_JDBC_URL} -Djdbc.user=${JWC_JDBC_USER} -Djdbc.password=${JWC_JDBC_PASS} -Djdbc.hibernate.dialect=${JWC_HIBERNATE_DIALECT}
