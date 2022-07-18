#!/bin/bash

SERVER_PID=`cat cassandra/server.pid`
if [[ ! -z $SERVER_PID ]]; then
    kill $SERVER_PID
fi