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
    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i ./ssh/jagger.vm.ssh.key jagger-jenkins@jagger-ci$1.vm.griddynamics.net $2
}

function do_on_vm_daemon {
    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i ./ssh/jagger.vm.ssh.key jagger-jenkins@jagger-ci$1.vm.griddynamics.net $2&
}

for i in 1 2
do
	echo TRYING TO DEPLOY NODE jagger-jenkins@jagger-ci$i.vm.griddynamics.net
	do_on_vm $i "rm -rf $JAGGER_HOME"
	do_on_vm $i "mkdir $JAGGER_HOME"

	scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i ./ssh/jagger.vm.ssh.key target/$PACKAGE jagger-jenkins@jagger-ci$i.vm.griddynamics.net:$JAGGER_HOME/$PACKAGE
	do_on_vm $i "unzip $JAGGER_HOME/$PACKAGE -d $JAGGER_HOME"

    echo KILLING PREVIOUS PROCESS jagger-jenkins@jagger-ci$i.vm.griddynamics.net
	do_on_vm $i "$JAGGER_HOME/$DISTRIB/stop.sh"
    do_on_vm $i "rm -rf /home/jagger-jenkins/jaggerdb"
done

echo sleep 3 sec
sleep 3

echo Starting Kernels
for i in 2
do
    echo "jagger-ci$i.vm.griddynamics.net : cd $JAGGER_HOME/jagger-$RELEASE_TYPE; ./start.sh profiles/ci-distributed/environment-kernel.properties"
	do_on_vm_daemon $i "cd $JAGGER_HOME/jagger-$RELEASE_TYPE; ./start.sh profiles/ci-distributed/environment-kernel.properties"
done

echo sleep 10 sec
sleep 10

echo Starting master
echo "jagger-ci1.vm.griddynamics.net cd $JAGGER_HOME/jagger-$RELEASE_TYPE; ./start.sh profiles/ci-distributed/environment-master.properties"
do_on_vm 1 "cd $JAGGER_HOME/jagger-$RELEASE_TYPE; ./start.sh profiles/ci-distributed/environment-master.properties"

exit