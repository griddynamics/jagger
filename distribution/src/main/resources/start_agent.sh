#!/bin/bash
echo $JAVA_HOME/bin/java -Xmx512m -Xms512m "$@" -classpath "./modules/chassis/*:./modules/diagnostics/*:./lib/*:./configuration/boot/" -Djava.library.path=./lib/native com.griddynamics.jagger.agent.AgentStarter
$JAVA_HOME/bin/java -Xmx512m -Xms512m "$@" -classpath "./modules/chassis/*:./modules/diagnostics/*:./lib/*:./configuration/boot/" -Djava.library.path=./lib/native com.griddynamics.jagger.agent.AgentStarter
