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

# generate swagger docs
mvn -pl jaas clean test

# copy swagger docs
swagger_html_path="./jaas/target/asciidoc/html/index.html"
swagger_pdf_path="./jaas/target/asciidoc/pdf/index.pdf"
swagger_docs_dest="./doc/swagger"

if [ ! -f ${swagger_html_path} ]; then
    echo "File $swagger_html_path not found!"
else
    if [ ! -d ${swagger_docs_dest} ]; then
        mkdir ${swagger_docs_dest}
    fi
    echo "Copying $swagger_html_path to $swagger_docs_dest..."
    cp ${swagger_html_path} ${swagger_docs_dest}/swagger_doc.html
fi

if [ ! -f ${swagger_pdf_path} ]; then
    echo "File $swagger_pdf_path not found!"
else
    if [ ! -d ${swagger_docs_dest} ]; then
        mkdir ${swagger_docs_dest}
    fi
    echo "Copying $swagger_pdf_path to $swagger_docs_dest..."
    cp ${swagger_pdf_path} ${swagger_docs_dest}/swagger_doc.pdf
fi