#!/bin/bash

TARGET=$1

if [ -z "$TARGET" ]; then
    echo "The first parameter must be the target directory"
    exit 1
fi

SRC=$(dirname $0)
mkdir -p $TARGET/.github/workflows
cp $SRC/../.github/workflows/maven.yml $TARGET/.github/workflows/
cp $SRC/../.github/workflows/release.yml $TARGET/.github/workflows/

rm -rf $TARGET/scripts
mkdir -p $TARGET/scripts
cp $SRC/keys.asc $TARGET/scripts/
cp $SRC/release.py $TARGET/scripts/
cp $SRC/../cliff.toml $TARGET/

pushd $TARGET
git add .
git update-index --chmod +x scripts/release.py
popd
