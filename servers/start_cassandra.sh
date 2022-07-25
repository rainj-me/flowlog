#!/bin/bash

export HEAP_NEWSIZE=300M
export MAX_HEAP_SIZE=1G

cassandra/bin/cassandra -p cassandra/server.pid