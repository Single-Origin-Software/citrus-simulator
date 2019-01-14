#!/bin/sh

which lsof
which jstack

lsof -PiTCP -sTCP:LISTEN
lsof -t -i:9001

PID=`lsof -t -i:9001`
echo $PID

if [[ "" !=  "$PID" ]]; then
  echo "Found process listening on port 9001: $PID"
  echo "Thread-Dump::Start"
  jstack -l  $PID
  echo "Thread-Dump::End"
else
  echo "No process found listening on port 9001"
fi
