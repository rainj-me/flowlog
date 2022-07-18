#!/bin/bash

TGZ_LINK=$1
TGZ_FILE=$2

if [[ -z $TGZ_FILE ]]; then
    echo "Require tarball file!"
    exit 2
elif [[ -z $TGZ_LINK ]]; then
    echo "Require download link!"
    exit 2
elif [[ -f $TGZ_FILE ]]; then
    echo "$TGZ_FILE exist!"
    exit 0
fi

wget $TGZ_LINK

