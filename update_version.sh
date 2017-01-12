#!/bin/bash

version=$1
folder=doc_$version

# When version defined
if [ ! -z "$version" ]
then
    echo "- Will look for folder '$folder'"

    # When folder exists
    if [[ -n $(find . -maxdepth 1 -mindepth 1 -type d -name $folder) ]]
    then
        echo "- Folder '$folder' found"

        echo "- Remove .gitignore files:"
        find $folder -name '.gitignore' | while read line; do
            echo $line
            rm $line
        done

        echo "- Replace actual docu with new one"
        rm -rf ./doc
        cp -r $folder ./doc

        echo "- Archive"
        rm -rf ./archive/$folder
        mv $folder ./archive/
        #Correct link
        sed -i.bak "s/..\/archive\/index.html/..\/index.html/g" ./archive/$folder/index.html

        echo "- Index"
        cd ./archive/
        ./create_index.sh
        cd ../

        echo "- Add to git"
        git add ./doc
        git add ./archive/$folder

    else
        echo "##### Error. Folder '$folder' not found"
    fi
else
    echo "##### Error. Version is not defined"
fi