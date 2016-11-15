FROM openjdk:8u92-jdk-alpine

ENV JAAS_VERSION 1.2.6-SNAPSHOT

ADD ["http://nexus.vm.griddynamics.net:8081/nexus/service/local/artifact/maven/content?r=jagger-snapshots&g=com.griddynamics.jagger&a=jaas&e=jar&v=${JAAS_VERSION}", "/com/griddynamics/jagger/jaas.jar"]

ADD run.sh /com/griddynamics/jagger/run.sh
RUN chmod +x /com/griddynamics/jagger/run.sh

WORKDIR /com/griddynamics/jagger

CMD /com/griddynamics/jagger/run.sh
