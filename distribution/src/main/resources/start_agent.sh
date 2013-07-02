#!/bin/bash
echo $JAVA_HOME/bin/java "$@" -classpath "./modules/chassis/*:./modules/diagnostics/*:./lib/*:./configuration/boot/" -Djava.library.path=./lib/native com.griddynamics.jagger.agent.AgentStarter
$JAVA_HOME/bin/java "$@" -classpath "./modules/chassis/*:./modules/diagnostics/*:./lib/*:./configuration/boot/" -Djava.library.path=./lib/native com.griddynamics.jagger.agent.AgentStarter
