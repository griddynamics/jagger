Jagger
======

Jagger is a free application suite for continuous performance testing

======

Requirements for local installation: java, maven, docker

All Jagger components for local installation can be separated to:
- Results storage and representation  components
- Load generating components

### Result storage and presentation components installation
Download docker compose .yml file for local installation `compose-2.0-package.zip` from
`https://nexus.griddynamics.net/nexus/content/repositories/jagger-releases/com/griddynamics/jagger/compose/2.0/` and unzip it

Run docker compose. All necessary images will be downloaded from the DockerHub and launched locally in containers

`docker-compose -f docker-compose.yml up`

After the installation you will get following components running as docker containers:
- MySQL database for test results storage
- Web UI for results representation, comparison, sharing. By default [http://localhost:8087/](http://localhost:8087/)
- MySQL database for Jagger as a Service (JaaS) configuration
- JaaS REST API. By default at [http://localhost:8088/jaas/swagger-ui.html#/](http://localhost:8088/jaas/swagger-ui.html#/)

### Load generating components installation
Create new test project from template

`mvn archetype:generate -DarchetypeGroupId=com.griddynamics.jagger -DarchetypeArtifactId=jagger-archetype-java-builders -DarchetypeVersion=2.0 -DarchetypeRepository=https://nexus.griddynamics.net/nexus/content/repositories/jagger-releases/`

Maven will ask you to enter `groupId`, `artifactId`, `version` and `package` name

After the installation you will get following components:
- Java maven project with `artifactId` name. Project will contain examples of the performance tests

### Running project

Compile

`mvn clean install`

Run test project

`cd ./target/{artifactdId}-{version}-full/`

`./start.sh profiles/basic/environment.properties`

Verify results
- Examine pdf report generated in the test execution folder
- View test results via Web UI. By default [http://localhost:8087/](http://localhost:8087/)
- Access test results or download pdf report via REST API. By default at [http://localhost:8088/jaas/swagger-ui.html#/](http://localhost:8088/jaas/swagger-ui.html#/)

### User manual
[http://griddynamics.github.io/jagger/doc/index.html](http://griddynamics.github.io/jagger/doc/index.html)
