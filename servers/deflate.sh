#!/bin/bash

TGZ_FILE=$1
TARGET_DIR=$2

if [[ -z $TGZ_FILE ]]; then
    echo "Require tarball file!"
    exit 2
elif [[ -z $TARGET_DIR ]]; then
    echo "Require target folder!"
    exit 2
elif [[ ! -f $TGZ_FILE ]]; then
    echo "$TGZ_FILE not exist!"
    exit 2
elif [[ -d $TARGET_DIR ]]; then
    echo "$TARGET_DIR already exist, skip!"
    exit
fi

echo "Extract $TGZ_FILE to $TARGET_DIR"
SOURCE_DIR_STR=`tar -ztf $TGZ_FILE |head -1`
if [[ "$?" != 0 ]]; then
    exit 2
fi

SOURCE_DIR=$(echo $SOURCE_DIR_STR | tr '/' '\n' |head -1)

tar zxf $TGZ_FILE > /dev/null
if [[ "$?" != "0" ]]; then
    echo "Extract failed!"
    exit 2
else
    mv $SOURCE_DIR $TARGET_DIR
    echo "Successful!"
    exit
fi

