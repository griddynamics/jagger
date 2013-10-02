#!/bin/bash

# xsd schema
rm -r ./doc/html/xsd/*.*

xsddoc -q -t "Jagger XML schema" -o ./doc/html/xsd/ ./chassis/spring.schema/src/main/resources/config-schema.xsd

# main docu
rm `ls ./doc/html/*.* | grep -v "header.html" | grep -v "tab-panel.css" | grep -v "tab-panel.js"`
#rm ./doc/html/*.*

doxygen ./doc/setup/Doxyfile

