FROM openjdk:8

ADD ["http://nexus.vm.griddynamics.net:8081/nexus/service/local/artifact/maven/content?r=jagger-snapshots&g=com.griddynamics.jagger&a=jaas&e=jar&v=${project.version}", "/com/griddynamics/jagger/jaas.jar"]

ADD run.sh /com/griddynamics/jagger/run.sh
RUN chmod +x /com/griddynamics/jagger/run.sh

# Update and install nc:
RUN apt-get update && apt-get install -y netcat

WORKDIR /com/griddynamics/jagger

CMD /com/griddynamics/jagger/run.sh
