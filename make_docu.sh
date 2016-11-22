#!/bin/bash

# Required software:
# 1. Doxygen (version 1.8.4): http://www.stack.nl/~dimitri/doxygen/download.html

version=$1

# set version - don't change text (doxygen will use it)
echo "Version: $version"
if [ ! -z "$version" ]; then
    echo "Version: $version" > ./doc/setup/JaggerVersion.txt
fi

# main docu
rm `ls ./doc/html/*.* | grep -v "header.html" | grep -v "tab-panel.css" | grep -v "tab-panel.js"`
doxygen ./doc/setup/Doxyfile

# copy docu for later usage by gh-pages branch script
if [ ! -z "$version" ]; then
    echo "Docu copy for version '$version' is created"
    cp -r ./doc/html ./doc_$version
fi