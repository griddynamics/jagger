#!/bin/sh
# Wait for database to get available

MYSQL_WAIT=${MYSQL_WAIT}

#wait for mysql
i=0
while ! nc $MYSQL_HOST $MYSQL_PORT >/dev/null 2>&1 < /dev/null; do
  i=`expr $i + 1`
  if [ $i -ge $MYSQL_WAIT ]; then
    echo "$(date) - ${MYSQL_HOST}:${MYSQL_PORT} still not reachable, giving up"
    exit 1
  fi
  echo "$(date) - waiting for ${MYSQL_HOST}:${MYSQL_PORT}..."
  sleep 1
done

#continue with further steps

#start the daemon
sleep 1
java -jar jagger-webclient.jar -httpPort=${JWC_HTTP_PORT} -Djdbc.driver=${JWC_JDBC_DRIVER} -Djdbc.url=${JWC_JDBC_URL} -Djdbc.user=${JWC_JDBC_USER} -Djdbc.password=${JWC_JDBC_PASS} -Djdbc.hibernate.dialect=${JWC_HIBERNATE_DIALECT}
