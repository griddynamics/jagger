#!/usr/bin/env bash

# Docker variables
IMAGE_NAME="jagger-web-client"
VERSION_TAG="1.3"
CONTAINER_NAME="jwc"
HTTP_HOST_PORT=8080

# Container env variables
JWC_HTTP_PORT=80
JWC_JDBC_DRIVER=com.mysql.jdbc.Driver
JWC_JDBC_URL=jdbc:mysql://172.18.128.228:3306/jaggerdb
JWC_JDBC_USER=root
JWC_JDBC_PASS=root

# Step 1. Check and build the image if needed.
echo "'Check and build the image if needed' phase for ${IMAGE_NAME} image..."
is_image_created=$(docker images -q ${IMAGE_NAME})
if [ -z "${is_image_created}" ]; then
    echo "Image ${IMAGE_NAME} is not built yet. Building..."
    cp -v ${HOME}/.m2/repository/com/griddynamics/jagger/webclient/${VERSION_TAG}-SNAPSHOT/webclient-${VERSION_TAG}-SNAPSHOT-exec-war.jar \
        jagger-webclient.jar

    docker build -t ${IMAGE_NAME}:${VERSION_TAG} .
    [[ $? -eq 0  ]] || { echo "Failed to build ${IMAGE_NAME} image." >&2; exit 1; }

    rm -fv jagger-webclient.jar
fi

# Step 2. Check and create the container if needed.
echo "'Check and create the container if needed' phase for ${CONTAINER_NAME} container..."
is_container_created=$(docker ps -aq -f name=${CONTAINER_NAME})
if [ -z "${is_container_created}" ]; then
    echo "Container ${CONTAINER_NAME} is not created. Creating..."
    docker \
      create \
      -p ${HTTP_HOST_PORT}:${JWC_HTTP_PORT} \
      --env JWC_HTTP_PORT=${JWC_HTTP_PORT} \
      --env JWC_JDBC_DRIVER=${JWC_JDBC_DRIVER} \
      --env JWC_JDBC_URL=${JWC_JDBC_URL} \
      --env JWC_JDBC_USER=${JWC_JDBC_USER} \
      --env JWC_JDBC_PASS=${JWC_JDBC_PASS} \
      --name ${CONTAINER_NAME} \
      ${IMAGE_NAME}:${VERSION_TAG}
    [[ $? -eq 0  ]] || { echo "Failed to create ${CONTAINER_NAME} container" >&2; exit 1; }
fi

# Step 3. Check and start the container if needed.
echo "'Check and start the container if needed' phase for ${CONTAINER_NAME} container..."
is_container_running=$(docker ps -q -f name=${CONTAINER_NAME})
if [ -z "${is_container_running}" ]; then
    echo "Container ${CONTAINER_NAME} is not started yet. Starting..."
    docker start ${CONTAINER_NAME}
    [[ $? -eq 0  ]] || { echo "Failed to start ${CONTAINER_NAME} container." >&2; exit 1; }
fi
