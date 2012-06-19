#!/bin/bash

if [ -z "$RELEASE_TYPE" ]
then
  RELEASE_TYPE=1.0.0-SNAPSHOT
  RELEASE_PLACE=snapshots
fi

chmod go-rwx ./ssh/jagger.vm.ssh.key

JAGGER_HOME=/home/jagger-ci/runned_jagger 
PACKAGE=jagger-distribution-$RELEASE_TYPE-full.zip
DISTRIB=jagger-distribution-$RELEASE_TYPE


function do_on_vm {
    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i ./ssh/jagger.vm.ssh.key jagger-ci@macys-jagger$1.vm.griddynamics.net $2
}

for i in 1
do
	echo TRYING TO DEPLOY NODE jagger-ci@macys-jagger$i.vm.griddynamics.net
	do_on_vm $i "rm -rf $JAGGER_HOME"
	do_on_vm $i "mkdir $JAGGER_HOME"

	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i ./ssh/jagger.vm.ssh.key target/$PACKAGE jagger-ci@macys-jagger$i.vm.griddynamics.net:$JAGGER_HOME/$PACKAGE
	do_on_vm $i "unzip $JAGGER_HOME/$PACKAGE -d $JAGGER_HOME"

    echo KILLING PREVIOUS PROCESS jagger-ci@macys-jagger$i.vm.griddynamics.net
	do_on_vm $i "$JAGGER_HOME/$DISTRIB/stop.sh"
    do_on_vm $i "rm -rf /home/jagger-ci/jaggerdb"
done

echo sleep 3 sec
sleep 3

echo Starting Jagger
for i in 1
do
    echo "macys-jagger$i.vm.griddynamics.net : cd $JAGGER_HOME/$DISTRIB; ./start.sh profiles/ci-local/environment.properties"
	do_on_vm $i "cd $JAGGER_HOME/$DISTRIB; ./start.sh profiles/ci-local/environment.properties"
done

echo sleep 3 sec
sleep 3

exit
