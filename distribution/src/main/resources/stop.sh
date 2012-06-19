#!/bin/bash
# deleting all processes with specified name
ps axwww | grep JaggerLauncher | awk '{print $1}' | xargs -n1 kill -9

