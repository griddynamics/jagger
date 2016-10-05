#!/usr/bin/env bash

MYSQL_USER="jaggeruser"
MYSQL_DATABASE="jaggerdb"
MYSQL_CONTAINER_NAME="jagger-mysql"

MYSQL_ROOT_PASSWORD="root"
MYSQL_PASSWORD="password"
MYSQL_EXTERNAL_PORT=3307
MYSQL_CONTAINER_PORT=3306

#MYSQL_ROOT_PASSWORD=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | sed 1q)
#MYSQL_PASSWORD=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | sed 1q)


is_container_running=$(docker inspect --format="{{ .State.Running }}" ${MYSQL_CONTAINER_NAME} 2> /dev/null)

if [ $? -eq 1 ]; then
    echo "Container ${MYSQL_CONTAINER_NAME} is not created. Creating and running..."
    docker \
      run \
      --detach \
      --env MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} \
      --env MYSQL_USER=${MYSQL_USER} \
      --env MYSQL_PASSWORD=${MYSQL_PASSWORD} \
      --env MYSQL_DATABASE=${MYSQL_DATABASE} \
      --name ${MYSQL_CONTAINER_NAME} \
      --publish ${MYSQL_EXTERNAL_PORT}:${MYSQL_CONTAINER_PORT} \
      mysql:5.7 \
      --sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY';
elif [ "$is_container_running" == "true" ]; then
    echo "Container ${MYSQL_CONTAINER_NAME} is already started and running. (try '$ docker ps')"
else
    echo "Container ${MYSQL_CONTAINER_NAME} is already created. Starting..."
    docker start ${MYSQL_CONTAINER_NAME}
fi
