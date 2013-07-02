#!/bin/bash
# deleting all processes with specified name
for PID in `pgrep -f AgentStarter`; do
    kill -9 $PID
done

