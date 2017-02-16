#!/bin/bash

# Required software:
# 1. Doxygen (version 1.8.4): http://www.stack.nl/~dimitri/doxygen/download.html

#with_swagger - set true if you want to generate swagger documentation for jaas
#version - version of the documentation. Will be on the main page of the doxygen documentation

with_swagger=$1
version=$2

if [ -z "$with_swagger" ]; then
    with_swagger=false
fi

echo "Use swagger: $with_swagger"
echo "Version: $version"

# set version - don't change text (doxygen will use it)
if [ ! -z "$version" ]; then
    echo "Version: $version" > ./doc/setup/JaggerVersion.txt
fi

# main docu
echo "=== Generating doxygen documentation ==="
rm `ls ./doc/html/*.* | grep -v "header.html" | grep -v "tab-panel.css" | grep -v "tab-panel.js"`
doxygen ./doc/setup/Doxyfile

# swagger docu
if [ "$with_swagger" = true ] ; then
    echo "=== Generating swagger documentation ==="

    swagger_html_path="./jaas/target/asciidoc/html/index.html"
    swagger_pdf_path="./jaas/target/asciidoc/pdf/index.pdf"
    swagger_docs_dest="./doc/html/swagger"

    rm_path=$swagger_docs_dest'/*.*'
    rm $rm_path

    # generate swagger docs
    mvn -pl jaas clean test

    # copy swagger docs
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
else
    echo "- Swagger documentation generation skipped"
fi # with swagger

# copy docu for later usage by gh-pages branch script
if [ ! -z "$version" ]; then
    echo "Docu copy for version '$version' is created"
    cp -r ./doc/html ./doc_$version
fi

