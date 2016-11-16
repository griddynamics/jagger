* <b>Install docker</b>

First of all install docker. https://docs.docker.com/engine/installation/
Make sure that it works. $ docker --help

After that you can use docker compose command for defining and running containers.

<b>Edit docker-compose.yml</b>

Edit docker-compose.yml file to change some parameters, such as mysql root password and network settings.
Docker compose use default network (172.17.0.1/16) to connect the containers.
Please note: if default network overlaps with your existing networks, you need to create new network (as example: 100.64.0.0/10) by using command:
"docker network create --driver=bridge --subnet=100.64.0.0/10 my_new_network". You can find more information and parameters at this link: https://docs.docker.com/engine/userguide/networking/ 
And specify this network in the docker file:
networks:
  default:
    external:
      name: my_new_network

<b>Use existing test results DB: </b>

We need to run only three containers: Jaas, Jaasdb and WebUI. And connect it to the existing database. 
Please edit docker-compose-wtih-existing-results-db.yml:
Need to add hostname into JWC_JDBC_URL, specify login/password on the JWC_JDBC_USER/JWC_JDBC_PASS on the jagger-web-client container section.
After that need to add hostname into JAGGER_DB_DEFAULT_URL, specify login/password on the JAGGER_DB_DEFAULT_USER/JAGGER_DB_DEFAULT_PASSWORD on the jagger-jaas container section.
After editing start three containers run "docker-compose -f docker-compose-wtih-existing-results-db.yml up -d".

<b>Use new test results DB: </b>

We need to run four containers: Jaas, Jaasdb, Jaggerdb and WebUI.
To start all four containers simply run "docker-compose -f docker-compose.yml up -d" on the docker/compose/ directory.
