First of all install docker. https://docs.docker.com/engine/installation/
Make sure that it works. $ docker --help

After that you can use docker compose command for defining and running containers.

Edit docker-compose.yml file to change some parameters, such as mysql root password and other (you can find more information and parameters at this link: https://hub.docker.com/_/mysql/).

To start mysql and jagger-web-client containers simply run "docker compose up -d". 
docker compose will run containers, and use default network (172.17.0.1/16) to connect the containers.
Please note: if default network overlaps with your existing networks, you need to create new network (as example: 100.64.0.0/10) by using command:
"docker network create --driver=bridge --subnet=100.64.0.0/10 my_new_network"
And specify this network in the docker file:
networks:
  default:
    external:
      name: my_new_network

