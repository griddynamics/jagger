First of all install docker. https://docs.docker.com/engine/installation/
Make sure that it works. $ docker --help

Edit start.sh script for change some parameters, such as mysql root password and so on (you can find more information and parameters at this link: https://hub.docker.com/_/mysql/).
To start container run start.sh script (don't forget to change permissions for .sh script).
If it's first time you running this script it'll create a new container and start it.
If container was already created, it'll just start it or do nothing if it's already running.

After that you can run any mysql client and connect to the db with credentials specified in the start.sh.

To stop docker container run: $ docker stop your_container_name
To remove docker container run: $ docker rm your_container_name
