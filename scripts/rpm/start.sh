#!/bin/sh
nohup java -Dserver.port=$DATASENSE_PORT -DDATASENSE_LOG_LEVEL=$DATASENSE_LOG_LEVEL -jar /opt/datasense/lib/datasense.war > /dev/null 2>&1  &
echo $! > /var/run/datasense/datasense.pid
