#!/bin/bash
FIRST_ARG="$1" # save the first argument because it will be not available after `shift`
shift # required to pass all positional arguments except first one
echo $JAVA_HOME/bin/java -Xmx2550m -Xms2550m "$@" -classpath "./modules/chassis/*:./modules/diagnostics/*:./lib/*:./configuration/boot/" -Dlog4j.configuration=jagger.log4j.properties com.griddynamics.jagger.JaggerLauncher $FIRST_ARG
$JAVA_HOME/bin/java -Xmx2550m -Xms2550m "$@" -classpath "./modules/chassis/*:./modules/diagnostics/*:./lib/*:./configuration/boot/" -Dlog4j.configuration=jagger.log4j.properties com.griddynamics.jagger.JaggerLauncher $FIRST_ARG
