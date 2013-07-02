#!/bin/bash
# deleting all processes with specified name
for PID in `pgrep -f JaggerLauncher `; do
    kill -9 $PID
done

