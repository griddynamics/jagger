#!/bin/bash
echo $JAVA_HOME/bin/java $1 $2 $3 $4 $5 $6 $7 $8 $9 -classpath "./modules/chassis/*:./modules/diagnostics/*:./lib/*:./configuration/boot/" -Djava.library.path=./lib/native com.griddynamics.jagger.agent.AgentStarter
$JAVA_HOME/bin/java $1 $2 $3 $4 $5 $6 $7 $8 $9 -classpath "./modules/chassis/*:./modules/diagnostics/*:./lib/*:./configuration/boot/" -Djava.library.path=./lib/native com.griddynamics.jagger.agent.AgentStarter
