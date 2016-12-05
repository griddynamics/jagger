#!/usr/bin/env bash
# Check if all important parameters exist:

: "${JAAS_DB_DRIVER:?Need to set JAAS_DB_DRIVER non-empty}"   
: "${JAAS_HTTP_PORT:?Need to set JAAS_HTTP_PORT non-empty}"   
: "${JAAS_DB_URL:?Need to set JAAS_DB_URL non-empty}"   
: "${JAAS_DB_USER:?Need to set JAAS_DB_USER non-empty}"   
: "${JAAS_DB_PASS:?Need to set JAAS_DB_PASS non-empty}"   
: "${JAAS_HIBERNATE_DIALECT:?Need to set JAAS_HIBERNATE_DIALECT non-empty}"   

# wait for mysql
while ! nc $MYSQL_HOST $MYSQL_PORT >/dev/null 2>&1 < /dev/null; do
  echo "$(date) - waiting for ${MYSQL_HOST}:${MYSQL_PORT}..."
  sleep 1
done

java -Djaas.hibernate.dialect=${JAAS_HIBERNATE_DIALECT} -Djaas.db.driver=${JAAS_DB_DRIVER} -Djaas.db.url=${JAAS_DB_URL} -Djaas.db.user=${JAAS_DB_USER} -Djaas.db.pass=${JAAS_DB_PASS} -Djagger.db.default.url=${JAGGER_DB_DEFAULT_URL} -Djagger.db.default.desc=${JAGGER_DB_DEFAULT_DESC} -Djagger.db.default.user=${JAGGER_DB_DEFAULT_USER} -Djagger.db.default.pass=${JAGGER_DB_DEFAULT_PASSWORD} -Djagger.db.default.jdbcDriver=${JAGGER_DB_DEFAULT_DRIVER} -Djagger.db.default.hibernateDialect=${JAGGER_DB_DEFAULT_HIBERNATE_DIALECT} -jar /com/griddynamics/jagger/jaas.jar  --server.port=${JAAS_HTTP_PORT}
