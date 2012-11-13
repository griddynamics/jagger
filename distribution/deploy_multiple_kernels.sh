#!/bin/bash

if [ -z "$RELEASE_TYPE" ]
then
  RELEASE_TYPE=1.0.0-SNAPSHOT
  RELEASE_PLACE=snapshots
fi

chmod go-rwx ./ssh/jagger.vm.ssh.key

JAGGER_HOME=/home/jagger-jenkins/runned_jagger
PACKAGE=jagger-distribution-$RELEASE_TYPE-full.zip
DISTRIB=jagger-distribution-$RELEASE_TYPE

function do_on_vm {
    echo remote run : $2
    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i ./ssh/jagger.vm.ssh.key jagger-jenkins@jagger-ci$1.vm.griddynamics.net $2
}

function do_on_vm_daemon {
    echo remote run like demon : $2
    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i ./ssh/jagger.vm.ssh.key jagger-jenkins@jagger-ci$1.vm.griddynamics.net $2&
}

for i in 1 2 3 4 5
do
	echo TRYING TO DEPLOY NODE jagger-jenkins@jagger-ci$i.vm.griddynamics.net
	do_on_vm $i "rm -rf $JAGGER_HOME"
	do_on_vm $i "mkdir $JAGGER_HOME"

	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i ./ssh/jagger.vm.ssh.key target/$PACKAGE jagger-jenkins@jagger-ci$i.vm.griddynamics.net:$JAGGER_HOME/$PACKAGE
	do_on_vm $i "unzip $JAGGER_HOME/$PACKAGE -d $JAGGER_HOME"

    echo KILLING PREVIOUS PROCESS jagger-jenkins@jagger-ci$i.vm.griddynamics.net
	do_on_vm $i "$JAGGER_HOME/$DISTRIB/stop.sh"
	do_on_vm $i "$JAGGER_HOME/$DISTRIB/stop_agent.sh"
    do_on_vm $i "rm -rf /home/jagger-jenkins/jaggerdb"
done

echo sleep 3 sec
sleep 3

echo Starting Kernels
for i in 2 3
do
    echo "jagger-ci$i.vm.griddynamics.net : cd $JAGGER_HOME/$DISTRIB; ./start.sh profiles/ci-distributed/environment-kernel.properties"
	do_on_vm_daemon $i "cd $JAGGER_HOME/$DISTRIB; ./start.sh profiles/ci-distributed/environment-kernel.properties $1 $2 $3 $4 $5 $6 $7 $8"
done

echo Starting Agents
for i in 4 5
do
    echo "jagger-ci$i.vm.griddynamics.net : cd $JAGGER_HOME/$DISTRIB; ./start_agent.sh"
	do_on_vm_daemon $i "cd $JAGGER_HOME/$DISTRIB; ./start_agent.sh -Dchassis.coordination.http.url=http://jagger-ci1.vm.griddynamics.net:8089 $1 $2 $3 $4 $5 $6 $7 $8"
done

echo sleep 3 sec
sleep 3

echo Starting master
echo "jagger-ci1.vm.griddynamics.net cd $JAGGER_HOME/$DISTRIB; ./start.sh profiles/ci-distributed/environment-master.properties"
do_on_vm 1 "cd $JAGGER_HOME/$DISTRIB; ./start.sh profiles/ci-distributed/environment-master.properties $1 $2 $3 $4 $5 $6 $7 $8"

exit